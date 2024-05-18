package com.bayu.billingservice.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    private String inputId;

    private String inputIPAddress;

    @NotBlank(message = "Customer Code cannot be empty")
    private String customerCode;

    private String subCode;

    @NotBlank(message = "Customer Name cannot be empty")
    private String customerName;

    @NotBlank(message = "Billing Category cannot be empty")
    private String billingCategory;

    @NotBlank(message = "Billing Type cannot be empty")
    private String billingType;

    @NotBlank(message = "Billing Template cannot be empty")
    private String billingTemplate;

    @NotBlank(message = "Currency cannot be empty")
    private String currency;

    @NotBlank(message = "MI Code cannot be empty")
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
