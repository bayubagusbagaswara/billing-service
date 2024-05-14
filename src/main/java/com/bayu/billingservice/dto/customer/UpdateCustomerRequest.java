package com.bayu.billingservice.dto.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateCustomerRequest {

    private String inputId;
    private String inputIPAddress;

    private Long id;

    // need a pattern (numeric & alphabet, not special character)
    @NotBlank(message = "Customer Code cannot be empty")
    private String customerCode;

    // need a pattern (numeric & alphabet, not special character)
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
    private String investmentManagementCode;

    private String investmentManagementName;

    // need a patter, must be numeric for string
    private String account;

    // need a patter, must be numeric for string
    private String costCenterDebit;

    // need a pattern (numeric & alphabet, not special character)
    private String accountName;

    // need a pattern, must be numeric for string
    private String glAccountHasil;

    // need a pattern must be numeric because big decimal
    private String customerMinimumFee;

    // need a pattern (must be numeric because big decimal)
    @NotBlank(message = "Customer Safekeeping Fee cannot be blank")
    private String customerSafekeepingFee;

    // need a pattern must be numeric because big decimal
    private String customerTransactionHandling;

    // need a pattern, must be numeric for string
    private String npwpNumber;

    // need a pattern (numeric & alphabet, not special character)
    private String npwpName;

    private String npwpAddress;

    // need a pattern, must be numeric for string
    @NotBlank(message = "Cost Center cannot be empty")
    private String costCenter;

    // need a pattern (numeric & alphabet, not special character)
    private String kseiSafeCode;

    // need a pattern (numeric & alphabet, not special character)
    private String sellingAgentCode;
}
