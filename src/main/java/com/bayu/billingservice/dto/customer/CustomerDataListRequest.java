package com.bayu.billingservice.dto.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDataListRequest {

    private String customerCode;
    private String subCode;
    private String customerName;
    private String billingCategory;
    private String billingType;
    private String billingTemplate;
    private String currency;
    private String miCode;
    private String miName;
    private String account;
    private String accountName;
    private String debitTransfer;
    private String costCenter;
    private String glAccountHasil;
    private String customerMinimumFee;
    private String customerSafekeepingFee;
    private String customerTransactionHandling;
    private String npwpNumber;
    private String npwpName;
    private String npwpAddress;
    private String kseiSafeCode;
    private String sellingAgent;
    private boolean gl;

}
