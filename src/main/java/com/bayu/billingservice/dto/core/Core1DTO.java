package com.bayu.billingservice.dto.core;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class Core1DTO extends BillingCoreBaseDTO {

    private String transactionHandlingValueFrequency;
    private String transactionHandlingFee;
    private String transactionHandlingAmountDue;

    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    private String subTotal;

    private String vatFee;
    private String vatAmountDue;

    private String totalAmountDue;
}
