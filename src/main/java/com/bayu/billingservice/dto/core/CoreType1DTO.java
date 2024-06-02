package com.bayu.billingservice.dto.core;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreType1DTO {

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
