package com.bayu.billingservice.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateKycRequest {

    private String aid;

    private String investmentManagementName;

    private String investmentManagementAddress;

    private String productName;

    private String accountName;

    private String accountNumber;

    private String accountBank;

    private String kseiSafeCode;

    private String minimumFee;

    private String customerFee;

    private String journal;

    private String billingCategory;

    private String billingType;

    private String billingTemplate;

}
