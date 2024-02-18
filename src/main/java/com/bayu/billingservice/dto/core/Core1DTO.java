package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Core1DTO {

    private String category;

    private String type;

    private String transactionHandlingValueFrequency;

    private String transactionHandlingFee;

    private String transactionHandlingAmountDue;

    private String safekeepingValueFrequency;

    private String safekeepingFee;

    private String safekeepingAmountDue;

    private String totalAmountBeforeVAT;

    private String vatFee;

    private String vatAmountDue;

    private String totalAmountDue;
}
