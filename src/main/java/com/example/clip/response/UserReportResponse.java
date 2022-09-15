package com.example.clip.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserReportResponse {
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("payments_sum")
    private BigDecimal paymentsSum;
    @JsonProperty("new_payments")
    private long newPayments;
    @JsonProperty("new_payments_amount")
    private BigDecimal newPaymentsAmount;
}
