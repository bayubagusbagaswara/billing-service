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

    private String approverId;
    private String approverIPAddress;

    private String dataChangeId;

    private AssetTransferCustomerDTO data;
}
