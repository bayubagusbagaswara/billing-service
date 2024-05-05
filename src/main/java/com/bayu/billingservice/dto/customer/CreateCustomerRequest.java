package com.bayu.billingservice.dto.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    private String inputId;
    private String inputIPAddress;

    @NotBlank(message = "Customer Code cannot be empty")
    private String customerCode;

    @NotBlank(message = "Customer Name cannot be empty")
    private String customerName;
    private BigDecimal customerMinimumFee;
    private BigDecimal customerSafekeepingFee;

    @NotBlank(message = "MI Code cannot be empty")
    private String investmentManagementCode;

    private String accountName;
    private String accountNumber;
    private String accountBank;

    private String kseiSafeCode;

    @NotBlank(message = "Billing Category cannot be empty")
    private String billingCategory;

    @NotBlank(message = "Billing Type cannot be empty")
    private String billingType;

    @NotBlank(message = "Billing Template cannot be empty")
    private String billingTemplate;

    @NotBlank(message = "Selling Agent Code cannot be empty")
    private String sellingAgentCode;

    @NotBlank(message = "Currency cannot be empty")
    private String currency;

}
