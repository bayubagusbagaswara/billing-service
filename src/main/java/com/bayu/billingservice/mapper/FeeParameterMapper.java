package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.FeeParameter;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeeParameterMapper {

    private final ModelMapper modelMapper;
    private final ConvertDateUtil convertDateUtil;

    public FeeParameterMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        this.modelMapper = modelMapper;
        this.convertDateUtil = convertDateUtil;
        configureMapper();
    }

    private void configureMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.addMappings(new PropertyMap<FeeParameter, FeeParameterDTO>() {
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
    }

    public FeeParameter mapFromDtoToEntity(FeeParameterDTO feeParameterDTO) {
        return modelMapper.map(feeParameterDTO, FeeParameter.class);
    }

    public FeeParameterDTO mapFromEntityToDto(FeeParameter feeParameter) {
        return modelMapper.map(feeParameter, FeeParameterDTO.class);
    }

    public List<FeeParameterDTO> mapToDTOList(List<FeeParameter> feeParameterList) {
        return feeParameterList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public FeeParameterDTO mapFromCreateRequestToDto(CreateFeeParameterRequest createFeeParameterRequest) {
        return modelMapper.map(createFeeParameterRequest, FeeParameterDTO.class);
    }

    public FeeParameterDTO mapFromUpdateRequestToDto(UpdateFeeParameterRequest updateFeeParameterRequest) {
        return modelMapper.map(updateFeeParameterRequest, FeeParameterDTO.class);
    }

    public FeeParameter createEntity(FeeParameterDTO feeParameterDTO, BillingDataChangeDTO dataChangeDTO) {
        FeeParameter feeParameter = modelMapper.map(feeParameterDTO, FeeParameter.class);
        feeParameter.setApprovalStatus(ApprovalStatus.APPROVED);
        feeParameter.setInputId(dataChangeDTO.getInputId());
        feeParameter.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        feeParameter.setInputDate(dataChangeDTO.getInputDate());
        feeParameter.setApproveId(dataChangeDTO.getApproveId());
        feeParameter.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        feeParameter.setApproveDate(convertDateUtil.getDate());
        return feeParameter;
    }

    public FeeParameter updateEntity(FeeParameter feeParameterUpdated, BillingDataChangeDTO dataChangeDTO) {
        FeeParameter feeParameter = modelMapper.map(feeParameterUpdated, FeeParameter.class);
        feeParameter.setApprovalStatus(ApprovalStatus.APPROVED);
        feeParameter.setInputId(dataChangeDTO.getInputId());
        feeParameter.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        feeParameter.setInputDate(dataChangeDTO.getInputDate());
        feeParameter.setApproveId(dataChangeDTO.getApproveId());
        feeParameter.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        feeParameter.setApproveDate(convertDateUtil.getDate());
        return feeParameter;
    }

    public void mapObjects(FeeParameterDTO feeParameterDTOSource, FeeParameter feeParameterTarget) {
        modelMapper.map(feeParameterDTOSource, feeParameterTarget);
    }

}
