package com.bayu.billingservice.dto.customer;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO extends ApprovalDTO {

    private Long id;

    @Pattern(regexp = "^[a-zA-Z0-9]{6}$", message = "Code must contain exactly 6 alphanumeric characters")
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

    @Pattern(regexp = "^\\d*$", message = "Account must contain only numeric digits")
    @NotBlank(message = "Account cannot be empty")
    private String account;

    @NotBlank(message = "Account Name cannot be empty")
    private String accountName;

    @Pattern(regexp = "^\\d*$", message = "Cost Center Debit must contain only numeric digits")
    private String debitTransfer;

    @Pattern(regexp = "^\\d*$", message = "Cost Center must contain only numeric digits")
    @NotBlank(message = "Cost Center cannot be empty")
    private String costCenter;

    @Pattern(regexp = "^\\d*$", message = "GL Account Hasil must contain only numeric digits")
    @NotBlank(message = "GL Account Hasil cannot be empty")
    private String glAccountHasil;

    @Pattern(regexp = "^\\d*$", message = "Customer Minimum Fee must contain only numeric digits")
    @NotBlank(message = "Customer Minimum Fee cannot be empty")
    private String customerMinimumFee;

    @NotEmpty(message = "Customer Safekeeping Fee cannot be empty")
    private String customerSafekeepingFee;

    @Pattern(regexp = "^\\d*$", message = "Transaction Handling must contain only numeric digits")
    private String customerTransactionHandling;

    @NotBlank(message = "NPWP Number cannot be empty")
    private String npwpNumber;

    @NotBlank(message = "NPWP Name cannot be empty")
    private String npwpName;

    @NotBlank(message = "NPWP Address cannot be empty")
    private String npwpAddress;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "KSEI Safe Code must contain only alphanumeric characters")
    private String kseiSafeCode;

    private String sellingAgent;

    private Boolean gl;

}
