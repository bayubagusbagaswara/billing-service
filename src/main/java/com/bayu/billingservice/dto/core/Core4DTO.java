package com.bayu.billingservice.dto.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class Core4DTO extends BillingCoreBaseDTO {

    // EB
    private String kseiSafekeepingAmountDue;

    private String kseiTransactionValueFrequency;
    private String kseiTransactionFee;
    private String kseiTransactionAmountDue;

    // Itama
    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    private String vatFee;
    private String vatAmountDue;

    // EB and Itama
    private String totalAmountDue;
}
