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
    public AssetTransferCustomerResponse createSingleData(CreateAssetTransferCustomerRequest createAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public AssetTransferCustomerResponse createSingleApprove(AssetTransferCustomerApproveRequest createAssetTransferCustomerListRequest) {
        return null;
    }

    @Override
    public AssetTransferCustomerResponse updateSingleData(UpdateAssetTransferCustomerRequest updateAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public AssetTransferCustomerResponse updateMultipleData(AssetTransferCustomerListRequest updateAssetTransferCustomerListRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public AssetTransferCustomerResponse updateSingleApprove(AssetTransferCustomerApproveRequest updateAssetTransferCustomerListRequest) {
        return null;
    }

    @Override
    public AssetTransferCustomerResponse deleteSingleData(DeleteAssetTransferCustomerRequest deleteAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public AssetTransferCustomerResponse deleteSingleApprove(AssetTransferCustomerApproveRequest deleteAssetTransferCustomerListRequest) {
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
