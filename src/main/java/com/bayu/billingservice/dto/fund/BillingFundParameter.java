package com.bayu.billingservice.dto.fund;

import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.SkTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingFundParameter {

    private Customer customer;
    private InvestmentManagementDTO investmentManagementDTO;
    private Instant dateNow;
    private String month;
    private int year;
    private List<SkTransaction> skTransactionList;
    private BigDecimal customerFee;
    private BigDecimal bis4TransactionFee;
    private BigDecimal kseiTransactionFee;
    private BigDecimal vatFee;

}
