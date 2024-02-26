package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Core2DTO {

    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    private String transactionValueFrequency;
    private String transactionFee;
    private String transactionAmountDue;

    private String totalAmountDueBeforeVat;

    private String vatFee;
    private String vatAmountDue;

    private String totalAmountDueAfterVat;

}
