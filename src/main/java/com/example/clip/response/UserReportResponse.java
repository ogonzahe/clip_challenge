package com.example.clip.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
