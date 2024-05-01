package com.bayu.billingservice.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * for single data request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    private String inputId;
    private String inputIPAddress;

    private String customerCode;
    private String customerMinimumFee;
    private String customerSafekeepingFee;

    private String investmentManagementName;
    private String investmentManagementAddressBuilding;
    private String investmentManagementAddressStreet;
    private String investmentManagementAddressCity;
    private String investmentManagementAddressProvince;

    private String accountName;
    private String accountNumber;
    private String accountBank;

    private String kseiSafeCode;

    private String billingCategory;
    private String billingType;
    private String billingTemplate;

}
