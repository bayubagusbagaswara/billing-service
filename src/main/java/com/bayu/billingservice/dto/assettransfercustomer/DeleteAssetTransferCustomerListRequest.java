package com.bayu.billingservice.dto.assettransfercustomer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAssetTransferCustomerListRequest {

    private String inputId;

    private String inputIPAddress;

    private String approveId;

    private String approveIPAddress;

    private List<DeleteAssetTransferCustomerDTO> assetTransferCustomerDTOList;
}
