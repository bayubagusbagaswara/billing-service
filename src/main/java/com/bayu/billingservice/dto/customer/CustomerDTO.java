package com.bayu.billingservice.dto.customer;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO extends ApprovalDTO {

    private Long dataChangeId;

    private Long id;

    // need a pattern (numeric & alphabet, not special character)
    @NotBlank(message = "Customer Code cannot be empty")
    private String customerCode;

    // need a pattern (numeric & alphabet, not special character)
    @NotEmpty(message = "Customer Name cannot be empty")
    private String customerName;

    @NotEmpty(message = "Billing Category cannot be empty")
    private String billingCategory;

    @NotEmpty(message = "Billing Type cannot be empty")
    private String billingType;

    @NotEmpty(message = "Billing Template cannot be empty")
    private String billingTemplate;

    @NotEmpty(message = "Currency cannot be empty")
    private String currency;

    @NotEmpty(message = "MI Code cannot be empty")
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
    @NotEmpty(message = "Customer Safekeeping Fee cannot be empty")
    private String customerSafekeepingFee;

    // need a pattern must be numeric because big decimal
    private String customerTransactionHandling;

    // need a pattern, must be numeric for string
    private String npwpNumber;

    // need a pattern (numeric & alphabet, not special character)
    private String npwpName;

    private String npwpAddress;

    // need a pattern, must be numeric for string
    @NotEmpty(message = "Cost Center cannot be empty")
    private String costCenter;

    // need a pattern (numeric & alphabet, not special character)
    private String kseiSafeCode;

    // need a pattern (numeric & alphabet, not special character)
    private String sellingAgentCode;


}
