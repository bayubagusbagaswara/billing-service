package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeparameter.*;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementResponse;
import com.bayu.billingservice.dto.investmentmanagement.UpdateInvestmentManagementRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FeeParameterService {

    boolean isCodeAlreadyExists(String code);

    public boolean isNameAlreadyExists(String name);

    List<FeeParameterDTO> getAll();

    FeeParameterDTO getByName(String name);

    BigDecimal getValueByName(String name);

    List<FeeParameterDTO> getByNameList(List<String> nameList);

    Map<String, BigDecimal> getValueByNameList(List<String> nameList);

    String deleteAll();

    FeeParameterResponse createSingleData(CreateFeeParameterRequest createFeeParameterRequest, BillingDataChangeDTO dataChangeDTO);

    FeeParameterResponse createMultipleData(CreateFeeParameterListRequest createFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO);

    FeeParameterResponse createSingleApprove(FeeParameterApproveRequest createFeeParameterListRequest, String clientIP);

    FeeParameterResponse updateSingleData(UpdateFeeParameterRequest updateFeeParameterRequest, BillingDataChangeDTO dataChangeDTO);

    FeeParameterResponse updateMultipleData(UpdateFeeParameterListRequest updateFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO);

    FeeParameterResponse updateSingleApprove(FeeParameterApproveRequest updateFeeParameterListRequest, String clientIP);

}
