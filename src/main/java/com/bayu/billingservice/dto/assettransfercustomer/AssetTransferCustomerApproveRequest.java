package com.bayu.billingservice.dto.assettransfercustomer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetTransferCustomerApproveRequest {

    private String approveId;
    private String approveIPAddress;

    private String dataChangeId;

    private AssetTransferCustomerDTO data;
}
