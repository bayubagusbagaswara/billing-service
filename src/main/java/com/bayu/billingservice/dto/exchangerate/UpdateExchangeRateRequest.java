package com.bayu.billingservice.dto.exchangerate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExchangeRateRequest {

    private String inputId;

    private String inputIPAddress;

    private Long id;

    private String date; // yyyy-MM-dd

    private String currency;

    private String value;
}
