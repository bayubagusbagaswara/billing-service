package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.sellingagent.SellingAgentDTO;
import com.bayu.billingservice.model.SellingAgent;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SellingAgentMapper extends BaseMapper<SellingAgent, SellingAgentDTO> {

    private final ConvertDateUtil convertDateUtil;

    public SellingAgentMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        super(modelMapper);
        this.convertDateUtil = convertDateUtil;
    }

    @Override
    protected PropertyMap<SellingAgent, SellingAgentDTO> getPropertyMap() {
        return new PropertyMap<SellingAgent, SellingAgentDTO>() {
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
    public SellingAgent mapToEntity(SellingAgentDTO dto) {
        return super.mapToEntity(dto);
    }

    @Override
    public SellingAgentDTO mapToDto(SellingAgent entity) {
        return super.mapToDto(entity);
    }

    @Override
    public List<SellingAgentDTO> mapToDTOList(List<SellingAgent> entityList) {
        return super.mapToDTOList(entityList);
    }

    @Override
    public SellingAgentDTO mapFromCreateRequestToDto(Object createRequest, Class<SellingAgentDTO> dtoClass) {
        return super.mapFromCreateRequestToDto(createRequest, dtoClass);
    }

    @Override
    public SellingAgentDTO mapFromUpdateRequestToDto(Object updateRequest, Class<SellingAgentDTO> dtoClass) {
        return super.mapFromUpdateRequestToDto(updateRequest, dtoClass);
    }

    @Override
    public SellingAgent createEntity(SellingAgentDTO dto, BillingDataChangeDTO dataChangeDTO) {
        return super.createEntity(dto, dataChangeDTO);
    }

    @Override
    public SellingAgent updateEntity(SellingAgent updatedEntity, Class<SellingAgentDTO> dto, BillingDataChangeDTO dataChangeDTO) {
        return super.updateEntity(updatedEntity, dto, dataChangeDTO);
    }

    @Override
    public void mapObjects(SellingAgentDTO sourceDto, SellingAgent targetEntity) {
        super.mapObjects(sourceDto, targetEntity);
    }

    @Override
    protected Class<SellingAgent> getEntityClass() {
        return SellingAgent.class;
    }

    @Override
    protected Class<SellingAgentDTO> getDtoClass() {
        return SellingAgentDTO.class;
    }

    @Override
    protected void setCommonProperties(SellingAgent entity, BillingDataChangeDTO dataChangeDTO) {
        entity.setApprovalStatus(ApprovalStatus.APPROVED);
        entity.setInputId(dataChangeDTO.getInputId());
        entity.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        entity.setInputDate(dataChangeDTO.getInputDate());
        entity.setApproveId(dataChangeDTO.getApproveId());
        entity.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        entity.setApproveDate(convertDateUtil.getDate());
    }

}
