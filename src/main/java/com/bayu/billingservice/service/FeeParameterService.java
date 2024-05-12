package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeparameter.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FeeParameterService {

    boolean isCodeAlreadyExists(String code);

    List<FeeParameterDTO> getAll();

    FeeParameterDTO getByName(String name);

    BigDecimal getValueByName(String name);

    List<FeeParameterDTO> getByNameList(List<String> nameList);

    Map<String, BigDecimal> getValueByNameList(List<String> nameList);

    String deleteAll();

    CreateFeeParameterListResponse createSingleData(CreateFeeParameterRequest createFeeParameterRequest, BillingDataChangeDTO dataChangeDTO);

    CreateFeeParameterListResponse createMultipleData(CreateFeeParameterListRequest createFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO);

    CreateFeeParameterListResponse createMultipleApprove(CreateFeeParameterListRequest createFeeParameterListRequest);

    UpdateFeeParameterListResponse updateMultipleData(UpdateFeeParameterListRequest updateFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateFeeParameterListResponse updateMultipleApprove(UpdateFeeParameterListRequest updateFeeParameterListRequest);
}
