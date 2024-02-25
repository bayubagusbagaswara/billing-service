package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.feeparameter.CreateFeeParameterRequest;
import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.FeeParameter;
import com.bayu.billingservice.repository.FeeParameterRepository;
import com.bayu.billingservice.service.FeeParameterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeParameterServiceImpl implements FeeParameterService {

    private final FeeParameterRepository feeParameterRepository;

    @Override
    public FeeParameterDTO create(CreateFeeParameterRequest request) {
        FeeParameter feeParameter = FeeParameter.builder()
                .name(request.getName())
                .description(request.getDescription())
                .value(Double.parseDouble(request.getValue()))
                .build();

        return mapToDTO(feeParameterRepository.save(feeParameter));
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
    public double getValueByName(String name) {
        FeeParameter feeParameter = feeParameterRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Fee Parameter not found with name : " + name));
        return feeParameter.getValue();
    }

    private static FeeParameterDTO mapToDTO(FeeParameter feeParameter) {
        return FeeParameterDTO.builder()
                .id(String.valueOf(feeParameter.getId()))
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
