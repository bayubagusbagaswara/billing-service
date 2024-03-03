package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.feeparameter.CreateFeeParameterRequest;
import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.FeeParameter;
import com.bayu.billingservice.repository.FeeParameterRepository;
import com.bayu.billingservice.service.FeeParameterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeParameterServiceImpl implements FeeParameterService {

    private final FeeParameterRepository feeParameterRepository;

    @Override
    public FeeParameterDTO create(CreateFeeParameterRequest request) {

        BigDecimal value = request.getValue().isEmpty() ? BigDecimal.ZERO :  new BigDecimal(request.getValue());

        FeeParameter feeParameter = FeeParameter.builder()
                .name(request.getName())
                .description(request.getDescription())
                .value(value)
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
    public BigDecimal getValueByName(String name) {
        FeeParameter feeParameter = feeParameterRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Fee Parameter not found with name : " + name));
        return feeParameter.getValue();
    }

    @Override
    public List<FeeParameterDTO> getByNameList(List<String> nameList) {
        List<FeeParameter> feeParameterList = feeParameterRepository.findFeeParameterByNameList(nameList);
        return mapToDTOList(feeParameterList);
    }

    @Override
    public Map<String, BigDecimal> getValueByNameList(List<String> nameList) {
        Map<String, BigDecimal> dataMap = new HashMap<>();

        List<FeeParameter> feeParameterList = feeParameterRepository.findFeeParameterByNameList(nameList);

        for (String name : nameList) {
            for (FeeParameter feeParameter : feeParameterList) {
                if (feeParameter.getName().equals(name)) {
                    dataMap.put(feeParameter.getName(), feeParameter.getValue());
                    break; // Optional: Exit the inner loop if a match is found
                }
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
