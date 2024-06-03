package com.bayu.billingservice.dto.fund;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BillingFundDTO extends BillingFundBaseDTO {

    private String accrualCustodialValueFrequency;
    private String accrualCustodialSafekeepingFee;
    private String accrualCustodialFee;

    private String bis4TransactionValueFrequency;
    private String bis4TransactionFee;
    private String bis4TransactionAmountDue;

    private String subTotal;

    private String vatFee;
    private String vatAmountDue;

    private String kseiTransactionValueFrequency;
    private String kseiTransactionFee;
    private String kseiTransactionAmountDue;

    private String totalAmountDue;

}
