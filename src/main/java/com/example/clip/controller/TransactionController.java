package com.example.clip.controller;

import java.math.RoundingMode;
import java.util.UUID;

import javax.persistence.PersistenceException;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clip.model.Payment;
import com.example.clip.model.enums.PaymentStatus;
import com.example.clip.repository.PaymentRepository;
import com.example.clip.request.PaymentRequest;
import com.example.clip.response.DisbursementResponse;
import com.example.clip.service.DisbursementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/clip/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final PaymentRepository paymentRepository;
    private final DisbursementService disbursementService;

    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(@RequestBody @Valid PaymentRequest paymentRequest) {
        String tracingId = UUID.randomUUID().toString();
        log.debug("{} - Request to create transaction received... {}", tracingId, paymentRequest);
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.NEW);
        payment.setAmount(paymentRequest.getAmount().setScale(2, RoundingMode.DOWN));
        payment.setUserId(paymentRequest.getUserId());

        try {
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Transaction {} Created Successfully", tracingId);
            log.debug("{} - Executed create transaction successfully...", tracingId);
            return ResponseEntity.ok(savedPayment);

        } catch (PersistenceException ex) {
            log.error("{} - Error creating transaction {}}", ex);
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @GetMapping("/disbursement")
    public ResponseEntity<DisbursementResponse> processDisbursement() {
        String tracingId = UUID.randomUUID().toString();
        log.debug("{} - Request to process disbursement received...", tracingId);
        DisbursementResponse disbursementResponse = disbursementService.processDisbursement();
        log.info("Disbursement processing {} was successful", tracingId);
        log.debug("{} - Executed process disbursement successfully...", tracingId);
        return ResponseEntity.ok().body(disbursementResponse);
    }

}
