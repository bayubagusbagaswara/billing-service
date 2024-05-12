package com.bayu.billingservice.dto.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateCustomerRequest {

    private String inputId;
    private String inputIPAddress;

    private Long id;

    @NotBlank(message = "Customer Code cannot be empty")
    private String customerCode;

    @NotBlank(message = "Customer Name cannot be empty")
    private String customerName;

    @NotNull(message = "Customer Minimum Fee cannot be null")
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
