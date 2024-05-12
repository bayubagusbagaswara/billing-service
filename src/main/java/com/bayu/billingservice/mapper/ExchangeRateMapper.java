package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.exchangerate.CreateExchangeRateRequest;
import com.bayu.billingservice.dto.exchangerate.ExchangeRateDTO;
import com.bayu.billingservice.dto.exchangerate.UpdateExchangeRateRequest;
import com.bayu.billingservice.model.ExchangeRate;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExchangeRateMapper {

    private final ModelMapperUtil modelMapperUtil;
    private final ConvertDateUtil convertDateUtil;

    public ExchangeRate mapFromDtoToEntity(ExchangeRateDTO exchangeRateDTO) {
        ExchangeRate exchangeRate = new ExchangeRate();
        modelMapperUtil.mapObjects(exchangeRateDTO, exchangeRate);
        return exchangeRate;
    }

    public ExchangeRateDTO mapFromEntityToDto(ExchangeRate exchangeRate) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<ExchangeRate, ExchangeRateDTO>() {
            @Override
            protected void configure() {
                skip(destination.getApprovalStatus());
                skip(destination.getInputId());
                skip(destination.getInputIPAddress());
                skip(destination.getInputDate());
                skip(destination.getApproveId());
                skip(destination.getApproveIPAddress());
                skip(destination.getApproveDate());
            }
        });

        return modelMapper.map(exchangeRate, ExchangeRateDTO.class);
    }

    public List<ExchangeRateDTO> mapToDTOList(List<ExchangeRate> exchangeRateList) {
        return exchangeRateList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public ExchangeRateDTO mapFromCreateExchangeRateRequestToDTO(CreateExchangeRateRequest createExchangeRateRequest) {
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        modelMapperUtil.mapObjects(createExchangeRateRequest, exchangeRateDTO);
        return exchangeRateDTO;
    }

    public ExchangeRate createEntity(ExchangeRateDTO exchangeRateDTO, BillingDataChangeDTO dataChangeDTO) {
        ExchangeRate exchangeRate = new ExchangeRate();
        modelMapperUtil.mapObjects(exchangeRateDTO, exchangeRate);
        exchangeRate.setApprovalStatus(ApprovalStatus.APPROVED);
        exchangeRate.setInputId(dataChangeDTO.getInputId());
        exchangeRate.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        exchangeRate.setInputDate(dataChangeDTO.getInputDate());
        exchangeRate.setApproveId(dataChangeDTO.getApproveId());
        exchangeRate.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        exchangeRate.setApproveDate(convertDateUtil.getDate());
        return exchangeRate;
    }

    public ExchangeRate updateEntity(ExchangeRate exchangeRateUpdated, BillingDataChangeDTO dataChangeDTO) {
        ExchangeRate exchangeRate = new ExchangeRate();
        modelMapperUtil.mapObjects(exchangeRateUpdated, exchangeRate);
        exchangeRate.setApprovalStatus(dataChangeDTO.getApprovalStatus());
        exchangeRate.setInputId(dataChangeDTO.getInputId());
        exchangeRate.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        exchangeRate.setInputDate(dataChangeDTO.getInputDate());
        exchangeRate.setApproveId(dataChangeDTO.getApproveId());
        exchangeRate.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        exchangeRate.setApproveDate(convertDateUtil.getDate());
        return exchangeRate;
    }

    public ExchangeRateDTO mapFromUpdateRequestToDto(UpdateExchangeRateRequest updateExchangeRateRequest) {
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        modelMapperUtil.mapObjects(updateExchangeRateRequest, exchangeRateDTO);
        return exchangeRateDTO;
    }

    public void mapObjects(ExchangeRateDTO exchangeRateDTOSource, ExchangeRate exchangeRateTarget) {
        modelMapperUtil.mapObjects(exchangeRateDTOSource, exchangeRateTarget);
    }

}
