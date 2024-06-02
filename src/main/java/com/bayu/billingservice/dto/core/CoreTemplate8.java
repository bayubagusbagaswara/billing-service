package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * type 10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreTemplate8 {

    private Integer transactionHandlingValueFrequency;
    private BigDecimal transactionHandlingFee;
    private BigDecimal transactionHandlingAmountDue;
}
