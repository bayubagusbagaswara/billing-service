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

    FeeParameterResponse createSingleData(CreateFeeParameterRequest createFeeParameterRequest, BillingDataChangeDTO dataChangeDTO);

    FeeParameterResponse createMultipleData(FeeParameterListRequest createFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO);

    FeeParameterResponse createSingleApprove(FeeParameterApproveRequest createFeeParameterListRequest);

    FeeParameterResponse updateMultipleData(FeeParameterListRequest updateFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO);

    FeeParameterResponse updateSingleApprove(FeeParameterApproveRequest updateFeeParameterListRequest);

}
