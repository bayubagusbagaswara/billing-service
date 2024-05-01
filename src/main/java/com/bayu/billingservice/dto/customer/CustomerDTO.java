package com.bayu.billingservice.dto.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO {

    private Long dataChangeId;

    private Long id;

    @NotBlank(message = "Customer Code cannot be empty")
    private String customerCode;

    @NotNull(message = "Customer Minimum Fee cannot be null")
    private BigDecimal customerMinimumFee;

    @NotNull(message = "Customer Safekeeping Fee cannot be null")
    private BigDecimal customerSafekeepingFee;

    @NotBlank(message = "MI Code cannot be empty")
    private String investmentManagementCode;
    private String investmentManagementName;

    private String accountName;
    private String accountNumber;
    private String costCenter;
    private String accountBank;

    private String kseiSafeCode;

    private String billingCategory;
    private String billingType;
    private String billingTemplate;
}
