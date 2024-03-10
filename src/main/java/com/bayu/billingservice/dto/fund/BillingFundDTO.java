package com.bayu.billingservice.dto.fund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingFundDTO {

    private String billingNumber;

    private String portfolioCode;

    private String period;

    private String amountDueAccrualCustody;

    private String valueFrequencyBis4;

    private String bis4Fee; // 23.000

    private String amountDueBis4;

    private String totalNominalBeforeTax;

    private String vatFee; // 0.11 (11%)

    private String amountDueVat;

    private String valueFrequencyKSEI;

    private String kseiFee; // 22.200

    private String amountDueKSEI;

    private String totalAmountDue;
}
