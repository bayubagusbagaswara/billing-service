package com.bayu.billingservice.dto.exchangerate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExchangeRateRequest {

    private String inputId;
    private String inputIPAddress;
    private String approveId;
    private String approveIPAddress;

    // data-data exchange rate yang akan diupdate
    private Long id;

    private BigDecimal value;
}
