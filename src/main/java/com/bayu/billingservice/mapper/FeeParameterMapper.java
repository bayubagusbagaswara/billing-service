package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeparameter.CreateFeeParameterRequest;
import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;
import com.bayu.billingservice.dto.feeparameter.UpdateFeeParameterListRequest;
import com.bayu.billingservice.model.FeeParameter;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeeParameterMapper {

    private final ModelMapperUtil modelMapperUtil;
    private final ConvertDateUtil convertDateUtil;

    public FeeParameter mapFromDtoToEntity(FeeParameterDTO feeParameterDTO) {
        FeeParameter feeParameter = new FeeParameter();
        modelMapperUtil.mapObjects(feeParameterDTO, feeParameter);
        return feeParameter;
    }

    public FeeParameterDTO mapFromEntityToDto(FeeParameter feeParameter) {
        ModelMapper modelMapper = modelMapperUtil.getModelMapper();
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
        return modelMapper.map(feeParameter, FeeParameterDTO.class);
    }

    public List<FeeParameterDTO> mapToDTOList(List<FeeParameter> feeParameterList) {
        return feeParameterList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public FeeParameterDTO mapFromCreateFeeParameterRequestToDto(CreateFeeParameterRequest createFeeParameterRequest) {
        FeeParameterDTO feeParameterDTO = new FeeParameterDTO();
        modelMapperUtil.mapObjects(createFeeParameterRequest, feeParameterDTO);
        return feeParameterDTO;
    }

    public FeeParameter createEntity(FeeParameterDTO feeParameterDTO, BillingDataChangeDTO dataChangeDTO) {
        FeeParameter feeParameter = new FeeParameter();
        modelMapperUtil.mapObjects(feeParameterDTO, feeParameter);
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
        FeeParameter feeParameter = new FeeParameter();
        modelMapperUtil.mapObjects(feeParameterUpdated, feeParameter);
        feeParameter.setApprovalStatus(ApprovalStatus.APPROVED);
        feeParameter.setInputId(dataChangeDTO.getInputId());
        feeParameter.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        feeParameter.setInputDate(dataChangeDTO.getInputDate());
        feeParameter.setApproveId(dataChangeDTO.getApproveId());
        feeParameter.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        feeParameter.setApproveDate(convertDateUtil.getDate());
        return feeParameter;
    }

    public FeeParameterDTO mapFromUpdateRequestToDto(UpdateFeeParameterListRequest updateFeeParameterListRequest) {
        FeeParameterDTO feeParameterDTO = new FeeParameterDTO();
        modelMapperUtil.mapObjects(updateFeeParameterListRequest, feeParameterDTO);
        return feeParameterDTO;
    }

    public void mapObjects(FeeParameterDTO feeParameterDTOSource, FeeParameter feeParameterTarget) {
        modelMapperUtil.mapObjects(feeParameterDTOSource, feeParameterTarget);
    }

}
