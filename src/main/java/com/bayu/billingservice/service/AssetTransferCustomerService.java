package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.assettransfercustomer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;

import java.util.List;

public interface AssetTransferCustomerService {
    CreateAssetTransferCustomerListResponse createSingleData(CreateAssetTransferCustomerRequest createAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO);

    CreateAssetTransferCustomerListResponse createMultipleApprove(CreateAssetTransferCustomerListRequest createAssetTransferCustomerListRequest);

    UpdateAssetTransferCustomerListResponse updateSingleData(UpdateAssetTransferCustomerRequest updateAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateAssetTransferCustomerListResponse updateMultipleData(UpdateAssetTransferCustomerListRequest updateAssetTransferCustomerListRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateAssetTransferCustomerListResponse updateMultipleApprove(UpdateAssetTransferCustomerListRequest updateAssetTransferCustomerListRequest);

    DeleteAssetTransferCustomerListResponse deleteSingleData(DeleteAssetTransferCustomerRequest deleteAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO);

    DeleteAssetTransferCustomerListResponse deleteMultipleApprove(DeleteAssetTransferCustomerListRequest deleteAssetTransferCustomerListRequest);

    List<AssetTransferCustomerDTO> getAll();

    String deleteAll();
}
