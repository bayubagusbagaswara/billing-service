package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * type 7.docx
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreTemplate2 {

    private BigDecimal safekeepingValueFrequency;
    private BigDecimal safekeepingFee;
    private BigDecimal safekeepingAmountDue;

    private BigDecimal vatFee;
    private BigDecimal vatAmountDue;

    private Integer kseiTransactionValueFrequency;
    private BigDecimal kseiTransactionFee;
    private BigDecimal kseiTransactionAmountDue;

    private BigDecimal kseiSafekeepingAmountDue;

    private BigDecimal totalAmountDue;

}
