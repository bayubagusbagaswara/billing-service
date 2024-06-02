package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * type 5, type 6 (With NPWP)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreTemplate4 {

    private Integer transactionHandlingValueFrequency;
    private BigDecimal transactionHandlingFee;
    private BigDecimal transactionHandlingAmountDue;
}
