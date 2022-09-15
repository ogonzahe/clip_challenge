package com.example.clip.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class PaymentRequest {
    @NotBlank
    @JsonProperty("user_id")
    String userId;
    @NotNull
    @PositiveOrZero
    BigDecimal amount;
}
