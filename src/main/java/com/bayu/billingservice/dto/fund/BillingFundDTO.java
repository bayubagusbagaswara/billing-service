package com.bayu.billingservice.dto.fund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingFundDTO {

    private String portfolioCode;

    private String period;

    private BigDecimal amountDueAccrualCustody;

    private Integer valueFrequencyS4;

    private BigDecimal s4Fee; // 23.000

    private BigDecimal amountDueS4;

    private BigDecimal totalNominalBeforeTax;

    private Double taxFee; // 0.11 (11%)

    private BigDecimal amountDueTax;

    private Integer valueFrequencyKSEI;

    private BigDecimal kseiFee; // 22.200

    private BigDecimal amountDueKSEI;

    private BigDecimal totalAmountDue;
}
