package com.bayu.billingservice.dto.assettransfercustomer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssetTransferCustomerRequest {

    private String inputerId;

    private String inputerIPAddress;

    private Long id;

    private String customerCode;

    private String securityCode;

    private String amount;

    private String effectiveDate;

    private String transferAssetType;

    private String isEnable;

}
