package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 17 OBAL (EB) - type 4b
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreTemplate5 {

    private Integer kseiTransactionValueFrequency;
    private BigDecimal kseiTransactionFee;
    private BigDecimal kseiTransactionAmountDue;

    private BigDecimal kseiSafekeepingAmountDue;

    private BigDecimal totalAmountDue;

}
