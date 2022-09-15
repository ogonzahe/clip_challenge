package com.example.clip.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DisbursementResult {
    private BigDecimal paymentsAmount;
    private BigDecimal amountToDisburse;
    private List<DisbursementDetail> disbursementDetails;
}
