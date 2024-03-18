package com.bayu.billingservice.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCustomerDTO {

    private Long id;

    private String customerCode; // alias AID

    private String investmentManagementName;

    private String investmentManagementAddress;

    private String accountName;

    private String accountNumber;

    private String costCenter;

    private String accountBank;

    private String kseiSafeCode;

    private BigDecimal customerMinimumFee;

    private BigDecimal customerSafekeepingFee;


    private String billingCategory;

    private String billingType;

    private String billingTemplate;
}
