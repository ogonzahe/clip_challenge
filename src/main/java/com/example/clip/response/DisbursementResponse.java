package com.example.clip.response;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DisbursementResponse {
    Map<String, DisbursementResult> disbursementDetails;
}
