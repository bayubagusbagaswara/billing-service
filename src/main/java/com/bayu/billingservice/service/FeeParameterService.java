package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.feeparameter.CreateFeeParameterRequest;
import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FeeParameterService {

    FeeParameterDTO create(CreateFeeParameterRequest request);

    List<FeeParameterDTO> getAll();

    FeeParameterDTO getByName(String name);

    BigDecimal getValueByName(String name);

    List<FeeParameterDTO> getByNameList(List<String> nameList);

    Map<String, BigDecimal> getValueByNameList(List<String> nameList);

    String deleteAll();
}
