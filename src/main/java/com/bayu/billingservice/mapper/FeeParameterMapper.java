package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;
import com.bayu.billingservice.model.FeeParameter;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeeParameterMapper extends BaseMapper<FeeParameter, FeeParameterDTO> {

    private final ConvertDateUtil convertDateUtil;

    public FeeParameterMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        super(modelMapper);
        this.convertDateUtil = convertDateUtil;
    }

    @Override
    protected PropertyMap<FeeParameter, FeeParameterDTO> getPropertyMap() {
        return new PropertyMap<FeeParameter, FeeParameterDTO>() {
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
        };
    }

    @Override
    public FeeParameter mapToEntity(FeeParameterDTO dto) {
        return super.mapToEntity(dto);
    }

    @Override
    public FeeParameterDTO mapToDto(FeeParameter entity) {
        return super.mapToDto(entity);
    }

    @Override
    public List<FeeParameterDTO> mapToDTOList(List<FeeParameter> entityList) {
        return super.mapToDTOList(entityList);
    }

    @Override
    public FeeParameterDTO mapFromCreateRequestToDto(Object createRequest, Class<FeeParameterDTO> dtoClass) {
        return super.mapFromCreateRequestToDto(createRequest, dtoClass);
    }

    @Override
    public FeeParameterDTO mapFromUpdateRequestToDto(Object updateRequest, Class<FeeParameterDTO> dtoClass) {
        return super.mapFromUpdateRequestToDto(updateRequest, dtoClass);
    }

    @Override
    public FeeParameter createEntity(FeeParameterDTO dto, BillingDataChangeDTO dataChangeDTO) {
        return super.createEntity(dto, dataChangeDTO);
    }

    @Override
    public FeeParameter updateEntity(FeeParameter updatedEntity, Class<FeeParameterDTO> dto, BillingDataChangeDTO dataChangeDTO) {
        return super.updateEntity(updatedEntity, dto, dataChangeDTO);
    }

    @Override
    public void mapObjects(FeeParameterDTO sourceDto, FeeParameter targetEntity) {
        super.mapObjects(sourceDto, targetEntity);
    }

    @Override
    protected Class<FeeParameter> getEntityClass() {
        return FeeParameter.class;
    }

    @Override
    protected Class<FeeParameterDTO> getDtoClass() {
        return FeeParameterDTO.class;
    }

    @Override
    protected void setCommonProperties(FeeParameter entity, BillingDataChangeDTO dataChangeDTO) {
        entity.setApprovalStatus(ApprovalStatus.APPROVED);
        entity.setInputId(dataChangeDTO.getInputId());
        entity.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        entity.setInputDate(dataChangeDTO.getInputDate());
        entity.setApproveId(dataChangeDTO.getApproveId());
        entity.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        entity.setApproveDate(convertDateUtil.getDate());
    }

}
