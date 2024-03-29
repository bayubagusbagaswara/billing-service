package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Core6WithNPWPDTO {

    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    private String bis4ValueFrequency;
    private String bis4Fee;
    private String bis4AmountDue;

    private String subTotal;

    private String vatFee;
    private String vatAmountDue;

    private String kseiTransactionValueFrequency;
    private String kseiTransactionFee;
    private String kseiTransactionAmountDue;

    private String kseiSafekeepingAmountDue;

    private String totalAmountDue;

}
