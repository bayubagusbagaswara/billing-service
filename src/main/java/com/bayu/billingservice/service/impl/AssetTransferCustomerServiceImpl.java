package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.assettransfercustomer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.repository.AssetTransferCustomerRepository;
import com.bayu.billingservice.service.AssetTransferCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetTransferCustomerServiceImpl implements AssetTransferCustomerService {

    private final AssetTransferCustomerRepository assetTransferCustomerRepository;

    @Override
    public CreateAssetTransferCustomerListResponse createSingleData(CreateAssetTransferCustomerRequest createAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public CreateAssetTransferCustomerListResponse createMultipleApprove(CreateAssetTransferCustomerListRequest createAssetTransferCustomerListRequest) {
        return null;
    }

    @Override
    public UpdateAssetTransferCustomerListResponse updateSingleData(UpdateAssetTransferCustomerRequest updateAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateAssetTransferCustomerListResponse updateMultipleData(UpdateAssetTransferCustomerListRequest updateAssetTransferCustomerListRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateAssetTransferCustomerListResponse updateMultipleApprove(UpdateAssetTransferCustomerListRequest updateAssetTransferCustomerListRequest) {
        return null;
    }

    @Override
    public DeleteAssetTransferCustomerListResponse deleteSingleData(DeleteAssetTransferCustomerRequest deleteAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public DeleteAssetTransferCustomerListResponse deleteMultipleApprove(DeleteAssetTransferCustomerListRequest deleteAssetTransferCustomerListRequest) {
        return null;
    }

    @Override
    public List<AssetTransferCustomerDTO> getAll() {
        return List.of();
    }

    @Override
    public String deleteAll() {
        return "";
    }
}
