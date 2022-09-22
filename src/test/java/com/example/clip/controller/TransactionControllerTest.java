package com.example.clip.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.example.clip.model.Payment;
import com.example.clip.model.enums.PaymentStatus;
import com.example.clip.repository.PaymentRepository;
import com.example.clip.request.PaymentRequest;
import com.example.clip.response.DisbursementResponse;
import com.example.clip.response.DisbursementResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
class TransactionControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PaymentRepository paymentRepository;

    private static final String ENDPOINT = "http://localhost:%s/api/clip/transactions/%s";
    private static final String DISBURSEMENT_FEE = "0.035";

    @Test
    void shouldCreatePaymentSuccessfully() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .userId("user-id-1")
                .amount(new BigDecimal(1L))
                .build();
        ResponseEntity<Payment> savedPayment = restTemplate.postForEntity(String.format(ENDPOINT, port, "create"),
                paymentRequest, Payment.class);
        assertEquals(HttpStatus.OK, savedPayment.getStatusCode());
        assertNotNull(savedPayment.getBody());
        assertEquals("user-id-1", savedPayment.getBody().getUserId());
        assertEquals(new BigDecimal(1L).setScale(2, RoundingMode.DOWN), savedPayment.getBody().getAmount());
    }

    @Test
    @Sql(scripts = "classpath:sql/transactions/process-disbursement-fixture.sql")
    void shouldProcessDisbursementSuccessfully() {
        ResponseEntity<DisbursementResponse> disbursementResponse = restTemplate
                .getForEntity(String.format(ENDPOINT, port, "disbursement"), DisbursementResponse.class);
        assertEquals(HttpStatus.OK, disbursementResponse.getStatusCode());
        Map<String, DisbursementResult> disbursementDetails = disbursementResponse.getBody().getDisbursementDetails();
        assertTrue(paymentRepository.findAllByStatus(PaymentStatus.NEW).isEmpty());
        assertEquals(2, disbursementDetails.size());
        assertEquals(new BigDecimal("12.80"), disbursementDetails.get("user-id-1").getPaymentsAmount());
        assertEquals(new BigDecimal("27.24"), disbursementDetails.get("user-id-2").getPaymentsAmount());
        assertEquals(
                new BigDecimal("12.80").subtract(new BigDecimal("12.80").multiply(new BigDecimal(DISBURSEMENT_FEE))).setScale(2,
                        RoundingMode.DOWN),
                disbursementDetails.get("user-id-1").getAmountToDisburse());
        assertEquals(
                new BigDecimal("27.24").subtract(new BigDecimal("27.24").multiply(new BigDecimal(DISBURSEMENT_FEE))).setScale(2,
                        RoundingMode.DOWN),
                disbursementDetails.get("user-id-2").getAmountToDisburse());
    }

}
