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
    private BigDecimal customerMinimumFee;
    private BigDecimal customerSafekeepingFee;

    private String investmentManagementName;
    private String investmentManagementAddressBuilding;
    private String investmentManagementAddressStreet;
    private String investmentManagementAddressCity;
    private String investmentManagementAddressProvince;


    private String accountName;
    private String accountNumber;
    private String costCenter;
    private String accountBank;

    private String kseiSafeCode;

    private String billingCategory;
    private String billingType;
    private String billingTemplate;
}
