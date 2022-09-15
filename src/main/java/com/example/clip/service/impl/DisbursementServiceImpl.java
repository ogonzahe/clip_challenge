package com.example.clip.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.clip.model.Payment;
import com.example.clip.model.enums.PaymentStatus;
import com.example.clip.repository.PaymentRepository;
import com.example.clip.response.DisbursementDetail;
import com.example.clip.response.DisbursementResponse;
import com.example.clip.response.DisbursementResult;
import com.example.clip.service.DisbursementService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisbursementServiceImpl implements DisbursementService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public DisbursementResponse processDisbursement() {
        List<Payment> paymentsToDisburse = paymentRepository.findAllByStatus(PaymentStatus.NEW);
        Map<String, DisbursementResult> disbursementDetails = new HashMap<>();
        computeDisbursement(paymentsToDisburse, disbursementDetails);
        roundAmounts(disbursementDetails);
        return DisbursementResponse.builder().disbursementDetails(disbursementDetails).build();
    }

    private void computeDisbursement(List<Payment> paymentsToDisburse, Map<String, DisbursementResult> disbursementDetails) {
        paymentsToDisburse.forEach(payment -> {
            disbursementDetails.compute(payment.getUserId(),
                    (userId, disbursementResult) -> Objects.isNull(disbursementResult)
                            ? DisbursementResult.builder()
                                                .paymentsAmount(payment.getAmount())
                                                .amountToDisburse(discountFee(payment.getAmount()))
                                                .disbursementDetails(Arrays.asList(
                                                    buildDisbursementDetail(payment)
                                                ))
                                                .build()
                            : DisbursementResult.builder()
                                                .paymentsAmount(disbursementResult.getPaymentsAmount().add(payment.getAmount()))
                                                .amountToDisburse(disbursementResult.getAmountToDisburse().add(discountFee(payment.getAmount())))
                                                .disbursementDetails(
                                                    Stream.concat(disbursementResult.getDisbursementDetails().stream(), Stream.of(buildDisbursementDetail(payment)))
                                                            .collect(Collectors.toList())
                                                )
                                                .build()
            );
            payment.setStatus(PaymentStatus.PROCESSED);
            paymentRepository.save(payment);
        });
    }

    private BigDecimal discountFee(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.965"));
    }

    private DisbursementDetail buildDisbursementDetail(Payment payment) {
        return DisbursementDetail.builder()
                                .paymentAmount(payment.getAmount())
                                .amountToDisburse(discountFee(payment.getAmount()))
                                .build();
    }

    private void roundAmounts(Map<String, DisbursementResult> disbursementDetails) {
        disbursementDetails.forEach((userId, disbursementResult) -> {
            disbursementResult.setAmountToDisburse(disbursementResult.getAmountToDisburse().setScale(2, RoundingMode.DOWN));
            disbursementResult.setPaymentsAmount(disbursementResult.getPaymentsAmount().setScale(2, RoundingMode.DOWN));
            disbursementResult.getDisbursementDetails().forEach(disbursementDetail -> {
                disbursementDetail.setAmountToDisburse(disbursementDetail.getAmountToDisburse().setScale(2, RoundingMode.DOWN));
                disbursementDetail.setPaymentAmount(disbursementDetail.getPaymentAmount().setScale(2, RoundingMode.DOWN));
            });
        });
    }
}
