package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.assettransfercustomer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;

import java.util.List;

public interface AssetTransferCustomerService {

    AssetTransferCustomerResponse createSingleData(CreateAssetTransferCustomerRequest createAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO);

    AssetTransferCustomerResponse createSingleApprove(AssetTransferCustomerApproveRequest createAssetTransferCustomerListRequest, String clientIP);

    AssetTransferCustomerResponse updateSingleData(UpdateAssetTransferCustomerRequest updateAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO);

    AssetTransferCustomerResponse updateSingleApprove(AssetTransferCustomerApproveRequest updateAssetTransferCustomerListRequest, String clientIP);

    AssetTransferCustomerResponse deleteSingleData(DeleteAssetTransferCustomerRequest deleteAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO);

    AssetTransferCustomerResponse deleteSingleApprove(AssetTransferCustomerApproveRequest deleteAssetTransferCustomerListRequest, String clientIP);

    String deleteAll();

    List<AssetTransferCustomerDTO> getAll();
}
