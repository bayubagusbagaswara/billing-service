package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.feeparameter.CreateFeeParameterRequest;
import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;

import java.util.List;

public interface FeeParameterService {

    FeeParameterDTO create(CreateFeeParameterRequest request);

    List<FeeParameterDTO> getAll();

    FeeParameterDTO getByName(String name);

    double getValueByName(String name);
}
