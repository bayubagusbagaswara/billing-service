package com.bayu.billingservice.dto.fund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundTemplate {

    private BigDecimal accrualCustodialValueFrequency;
    private BigDecimal accrualCustodialSafekeepingFee;
    private BigDecimal accrualCustodialFee;

    private Integer bis4TransactionValueFrequency;
    private BigDecimal bis4TransactionFee;
    private BigDecimal bis4TransactionAmountDue;

    private BigDecimal subTotal;
    private BigDecimal vatFee;
    private BigDecimal vatAmountDue;

    private Integer kseiTransactionValueFrequency;
    private BigDecimal kseiTransactionFee;
    private BigDecimal kseiTransactionAmountDue;

    private BigDecimal totalAmountDue;
}
