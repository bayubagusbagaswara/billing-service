package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * type 3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreTemplate7 {

    private Integer transactionHandlingValueFrequency;
    private BigDecimal transactionHandlingFee;
    private BigDecimal transactionHandlingAmountDue;
}
