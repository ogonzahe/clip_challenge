package com.example.clip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.clip.model.Payment;
import com.example.clip.model.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query(value = "SELECT DISTINCT user_id FROM payment", nativeQuery = true)
    List<String> findAllUsers();
    List<Payment> findAllByUserId(String userId);
    List<Payment> findAllByStatus(PaymentStatus paymentStatus);
}
