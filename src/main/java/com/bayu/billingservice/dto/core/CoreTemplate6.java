package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * IIG - type 8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreTemplate6 {

    private Integer transactionHandlingValueFrequency;
    private BigDecimal transactionHandlingFee;
    private BigDecimal transactionHandlingAmountDue;
}
