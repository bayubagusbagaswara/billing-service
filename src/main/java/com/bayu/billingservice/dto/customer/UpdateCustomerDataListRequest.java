package com.bayu.billingservice.dto.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerDataListRequest {

    @JsonProperty(value = "Customer Code")
    private String customerCode;

    @JsonProperty(value = "Sub Code")
    private String subCode;

    @JsonProperty(value = "Customer Name")
    private String customerName;

    @JsonProperty(value = "Billing Category")
    private String billingCategory;

    @JsonProperty(value = "Billing Type")
    private String billingType;

    @JsonProperty(value = "Billing Template")
    private String billingTemplate;

    @JsonProperty(value = "Currency")
    private String currency;

    @JsonProperty(value = "MI Code")
    private String miCode;

    @JsonProperty(value = "Account")
    private String account;

    @JsonProperty(value = "Account Name")
    private String accountName;

    @JsonProperty(value = "Cost Center Debit")
    private String debitTransfer;

    @JsonProperty(value = "Cost Center")
    private String costCenter;

    @JsonProperty(value = "GL Account Hasil")
    private String glAccountHasil;

    @JsonProperty(value = "Minimum Fee")
    private String customerMinimumFee;

    @JsonProperty(value = "Customer Safekeeping Fee")
    private String customerSafekeepingFee;

    @JsonProperty(value = "Transaction Handling")
    private String customerTransactionHandling;

    @JsonProperty(value = "NPWP")
    private String npwpNumber;

    @JsonProperty(value = "Nama NPWP")
    private String npwpName;

    @JsonProperty(value = "Alamat NPWP")
    private String npwpAddress;

    @JsonProperty(value = "KSEI Safe Code")
    private String kseiSafeCode;

    @JsonProperty(value = "Selling Agent")
    private String sellingAgent;

    @JsonProperty(value = "Is GL")
    private String gl;
}
