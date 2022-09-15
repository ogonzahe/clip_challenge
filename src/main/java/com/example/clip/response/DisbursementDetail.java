package com.example.clip.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DisbursementDetail {
    private BigDecimal paymentAmount;
    private BigDecimal amountToDisburse;
}
