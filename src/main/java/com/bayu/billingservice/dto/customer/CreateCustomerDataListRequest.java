package com.bayu.billingservice.dto.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerDataListRequest {

    @JsonProperty(value = "Customer Code")
    @NotBlank(message = "Customer Code cannot be empty")
    private String customerCode;

    @JsonProperty(value = "Sub Code")
    @Pattern(regexp = "^[a-zA-Z0-9]{6}$", message = "Code must contain exactly 6 alphanumeric characters")
    @NotBlank(message = "Customer Code cannot be empty")
    private String subCode;

    @JsonProperty(value = "Customer Name")
    @NotBlank(message = "Customer Name cannot be empty")
    private String customerName;

    @JsonProperty(value = "Billing Category")
    @NotBlank(message = "Billing Category cannot be empty")
    private String billingCategory;

    @JsonProperty(value = "Billing Type")
    @NotBlank(message = "Billing Type cannot be empty")
    private String billingType;

    @JsonProperty(value = "Billing Template")
    @NotBlank(message = "Billing Template cannot be empty")
    private String billingTemplate;

    @JsonProperty(value = "Currency")
    @NotBlank(message = "Currency cannot be empty")
    private String currency;

    @JsonProperty(value = "MI Code")
    @NotBlank(message = "MI Code cannot be empty")
    private String miCode;

    @JsonProperty(value = "Account")
    @Pattern(regexp = "^\\d*$", message = "Account must contain only numeric digits")
    @NotBlank(message = "Account cannot be empty")
    private String account;

    @JsonProperty(value = "Account Name")
    @NotBlank(message = "Account Name cannot be empty")
    private String accountName;

    @JsonProperty(value = "Cost Center Debit")
    @Pattern(regexp = "^\\d*$", message = "Cost Center Debit must contain only numeric digits")
    private String debitTransfer;

    @JsonProperty(value = "Cost Center")
    @Pattern(regexp = "^\\d*$", message = "Cost Center must contain only numeric digits")
    @NotBlank(message = "Cost Center cannot be empty")
    private String costCenter;

    @JsonProperty(value = "GL Account Hasil")
    @Pattern(regexp = "^\\d*$", message = "GL Account Hasil must contain only numeric digits")
    @NotBlank(message = "GL Account Hasil cannot be empty")
    private String glAccountHasil;

    @JsonProperty(value = "Minimum Fee")
    @Pattern(regexp = "^\\d*$", message = "Customer Minimum Fee must contain only numeric digits")
    @NotBlank(message = "Customer Minimum Fee cannot be empty")
    private String customerMinimumFee;

    @JsonProperty(value = "Customer Safekeeping Fee")
    @NotEmpty(message = "Customer Safekeeping Fee cannot be empty")
    private String customerSafekeepingFee;

    @JsonProperty(value = "Transaction Handling")
    @Pattern(regexp = "^\\d*$", message = "Transaction Handling must contain only numeric digits")
    private String customerTransactionHandling;

    @JsonProperty(value = "NPWP Number")
    @NotBlank(message = "NPWP Number cannot be empty")
    private String npwpNumber;

    @JsonProperty(value = "NPWP Name")
    @NotBlank(message = "NPWP Name cannot be empty")
    private String npwpName;

    @JsonProperty(value = "NPWP Address")
    @NotBlank(message = "NPWP Address cannot be empty")
    private String npwpAddress;

    @JsonProperty(value = "KSEI Safe Code")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "KSEI Safe Code must contain only alphanumeric characters")
    private String kseiSafeCode;

    @JsonProperty(value = "Selling Agent")
    private String sellingAgent;

    @JsonProperty(value = "Is GL")
    @NotBlank(message = "Is GL cannot be empty")
    private String gl;

}
