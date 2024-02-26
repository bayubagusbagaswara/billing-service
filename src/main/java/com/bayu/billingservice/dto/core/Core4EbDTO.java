package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Core4EbDTO {

    private String kseiSafekeepingAmountDue;
    private String kseiTransactionValueFrequency;
    private String kseiTransactionFee;
    private String kseiTransactionAmountDue;
    private String totalAmountDue;

}
