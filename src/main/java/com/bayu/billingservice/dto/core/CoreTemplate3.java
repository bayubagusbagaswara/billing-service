package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreTemplate3 {

    private Integer transactionHandlingValueFrequency;
    private BigDecimal transactionHandlingFee;
    private BigDecimal transactionHandlingAmountDue;

    private BigDecimal safekeepingValueFrequency;
    private BigDecimal safekeepingFee;
    private BigDecimal safekeepingAmountDue;

    private BigDecimal subTotal;

    private BigDecimal vatFee;
    private BigDecimal vatAmountDue;

    private BigDecimal totalAmountDue;
}
