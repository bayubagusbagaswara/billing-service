package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * type 5, type 6 (without NPWP)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreTemplate1 {

    private BigDecimal safekeepingValueFrequency;
    private BigDecimal safekeepingFee;
    private BigDecimal safekeepingAmountDue;

    private Integer kseiTransactionValueFrequency;
    private BigDecimal kseiTransactionFee;
    private BigDecimal kseiTransactionAmountDue;

    private Integer bis4TransactionValueFrequency;
    private BigDecimal bis4TransactionFee;
    private BigDecimal bis4TransactionAmountDue;

    private BigDecimal kseiSafekeepingAmountDue;

    private BigDecimal totalAmountDue;

}
