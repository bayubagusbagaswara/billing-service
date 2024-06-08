package com.bayu.billingservice.dto.fund;

import com.bayu.billingservice.model.SkTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingFundParameter {

    private BigDecimal customerFee; // accrual custodial safekeeping value frequency

    private List<SkTransaction> skTransactionList;

    private BigDecimal customerSafekeepingFee;

    private BigDecimal bis4TransactionFee;

    private BigDecimal kseiTransactionFee;

    private BigDecimal vatFee;
}
