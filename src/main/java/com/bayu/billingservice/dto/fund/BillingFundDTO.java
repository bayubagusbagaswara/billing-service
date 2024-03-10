package com.bayu.billingservice.dto.fund;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BillingFundDTO extends BillingFundBaseDTO {

    private String customerFee;
    private String accrualCustodialFee;
    private String bis4ValueFrequency;
    private String bis4TransactionFee;
    private String bis4AmountDue;
    private String subTotal;
    private String vatFee;
    private String vatAmountDue;
    private String kseiValueFrequency;
    private String kseiTransactionFee;
    private String kseiAmountDue;
    private String totalAmountDue;
}
