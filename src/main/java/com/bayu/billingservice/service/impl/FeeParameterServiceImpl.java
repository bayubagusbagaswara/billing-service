package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeparameter.*;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.mapper.FeeParameterMapper;
import com.bayu.billingservice.model.FeeParameter;
import com.bayu.billingservice.repository.FeeParameterRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.service.FeeParameterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeParameterServiceImpl implements FeeParameterService {

    private static final String ID_NOT_FOUND = "Investment Management not found with id: ";
    private static final String CODE_NOT_FOUND = "Investment Management not found with code: ";
    private static final String UNKNOWN = "unknown";

    private final FeeParameterRepository feeParameterRepository;
    private final BillingDataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final FeeParameterMapper feeParameterMapper;

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return false;
    }

    @Override
    public FeeParameterResponse createSingleData(CreateFeeParameterRequest createFeeParameterRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public FeeParameterResponse createMultipleData(FeeParameterListRequest createFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public FeeParameterResponse createSingleApprove(FeeParameterApproveRequest createFeeParameterListRequest) {
        return null;
    }

    @Override
    public FeeParameterResponse updateMultipleData(FeeParameterListRequest updateFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public FeeParameterResponse updateSingleApprove(FeeParameterApproveRequest updateFeeParameterListRequest) {
        return null;
    }

    @Override
    public List<FeeParameterDTO> getAll() {
        return mapToDTOList(feeParameterRepository.findAll());
    }

    @Override
    public FeeParameterDTO getByName(String name) {
        return mapToDTO(feeParameterRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Fee Parameter not found with name : " + name)));
    }

    @Override
    public BigDecimal getValueByName(String name) {
        FeeParameter feeParameter = feeParameterRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Fee Parameter not found with name : " + name));
        return feeParameter.getValue();
    }

    @Override
    public List<FeeParameterDTO> getByNameList(List<String> nameList) {
        List<FeeParameter> feeParameterList = feeParameterRepository.findFeeParameterByNameList(nameList);
        // Check if all names are present in the feeParameterList
        for (String name : nameList) {
            Optional<FeeParameter> foundParameter = feeParameterList.stream()
                    .filter(parameter -> parameter.getName().equals(name))
                    .findFirst();

            if (foundParameter.isEmpty()) {
                // If a name is not found, you can throw a custom exception or handle it as needed
                throw new DataNotFoundException("FeeParameter with name '" + name + "' not found");
            }
        }

        return mapToDTOList(feeParameterList);
    }

    @Override
    public Map<String, BigDecimal> getValueByNameList(List<String> nameList) {
        List<FeeParameter> feeParameterList = feeParameterRepository.findFeeParameterByNameList(nameList);

        Map<String, BigDecimal> dataMap = feeParameterList.stream()
                .collect(Collectors.toMap(FeeParameter::getName, FeeParameter::getValue));

        // Check if all names are present in the dataMap
        for (String name : nameList) {
            if (!dataMap.containsKey(name)) {
                // If a name is not found, you can throw a custom exception or handle it as needed
                throw new DataNotFoundException("FeeParameter with name '" + name + "' not found");
            }
        }

        return dataMap;
    }

    @Override
    public String deleteAll() {
        try {
            feeParameterRepository.deleteAll();
            return "Successfully deleted all Fee Parameter";
        } catch (Exception e) {
            log.error("Error when delete all Fee Parameter : " + e.getMessage());
            throw new ConnectionDatabaseException("Error when delete all Fee Parameter");
        }
    }

    private static FeeParameterDTO mapToDTO(FeeParameter feeParameter) {
        return FeeParameterDTO.builder()
//                .id(String.valueOf(feeParameter.getId()))
                .name(feeParameter.getName())
                .value(String.valueOf(feeParameter.getValue()))
                .description(feeParameter.getDescription())
                .build();
    }

    private static List<FeeParameterDTO> mapToDTOList(List<FeeParameter> feeParameterList) {
        return feeParameterList.stream()
                .map(FeeParameterServiceImpl::mapToDTO)
                .toList();
    }

}
