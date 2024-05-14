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
public class AssetTransferCustomerListRequest {

    private String inputId;
    private String inputIPAddress;

    private List<AssetTransferCustomerDTO> assetTransferCustomerDTOList;

}