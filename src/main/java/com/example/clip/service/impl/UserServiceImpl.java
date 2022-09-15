package com.example.clip.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.clip.exception.UserNotFoundException;
import com.example.clip.model.Payment;
import com.example.clip.model.enums.PaymentStatus;
import com.example.clip.repository.PaymentRepository;
import com.example.clip.response.ListAllUsersResponse;
import com.example.clip.response.UserReportResponse;
import com.example.clip.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PaymentRepository paymentRepository;

    @Override
    public ListAllUsersResponse listAll() {
        List<String> usersList = paymentRepository.findAllUsers();
        return ListAllUsersResponse.builder().usersList(usersList).build();
    }

    @Override
    public UserReportResponse getUserReport(String userId) throws UserNotFoundException {
        List<Payment> userPayments = paymentRepository.findAllByUserId(userId);
        if(userPayments.isEmpty()) {
            throw new UserNotFoundException(String.format("User %s does not exists", userId)); 
        }
        final BigDecimal paymentsSum = userPayments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal newPaymentsAmount = userPayments.stream().filter(userPayment -> PaymentStatus.NEW.equals(userPayment.getStatus())).map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        final long newPayments = userPayments.stream().filter(userPayment -> PaymentStatus.NEW.equals(userPayment.getStatus())).count();
        return UserReportResponse.builder()
                                    .userName(userId)
                                    .paymentsSum(paymentsSum.setScale(2, RoundingMode.DOWN))
                                    .newPayments(newPayments)
                                    .newPaymentsAmount(newPaymentsAmount.setScale(2, RoundingMode.DOWN))
                                    .build();
    }

}
