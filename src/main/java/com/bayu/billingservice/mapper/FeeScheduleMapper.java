package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.FeeScheduleDTO;
import com.bayu.billingservice.model.FeeSchedule;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeeScheduleMapper extends BaseMapper<FeeSchedule, FeeScheduleDTO> {

    private final ConvertDateUtil convertDateUtil;

    public FeeScheduleMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        super(modelMapper);
        this.convertDateUtil = convertDateUtil;
    }

    @Override
    protected PropertyMap<FeeSchedule, FeeScheduleDTO> getPropertyMap() {
        return new PropertyMap<FeeSchedule, FeeScheduleDTO>() {
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
    public FeeSchedule mapToEntity(FeeScheduleDTO dto) {
        return super.mapToEntity(dto);
    }

    @Override
    public FeeScheduleDTO mapToDto(FeeSchedule entity) {
        return super.mapToDto(entity);
    }

    @Override
    public List<FeeScheduleDTO> mapToDTOList(List<FeeSchedule> entityList) {
        return super.mapToDTOList(entityList);
    }

    @Override
    public FeeScheduleDTO mapFromCreateRequestToDto(Object createRequest, Class<FeeScheduleDTO> dtoClass) {
        return super.mapFromCreateRequestToDto(createRequest, dtoClass);
    }

    @Override
    public FeeScheduleDTO mapFromUpdateRequestToDto(Object updateRequest, Class<FeeScheduleDTO> dtoClass) {
        return super.mapFromUpdateRequestToDto(updateRequest, dtoClass);
    }

    @Override
    public FeeSchedule createEntity(FeeScheduleDTO dto, BillingDataChangeDTO dataChangeDTO) {
        return super.createEntity(dto, dataChangeDTO);
    }

    @Override
    protected Class<FeeSchedule> getEntityClass() {
        return FeeSchedule.class;
    }

    @Override
    public FeeSchedule updateEntity(FeeSchedule updatedEntity, Class<FeeScheduleDTO> dto, BillingDataChangeDTO dataChangeDTO) {
        return super.updateEntity(updatedEntity, dto, dataChangeDTO);
    }

    @Override
    public void mapObjects(FeeScheduleDTO sourceDto, FeeSchedule targetEntity) {
        super.mapObjects(sourceDto, targetEntity);
    }

    @Override
    protected Class<FeeScheduleDTO> getDtoClass() {
        return FeeScheduleDTO.class;
    }

    @Override
    protected void setCommonProperties(FeeSchedule entity, BillingDataChangeDTO dataChangeDTO) {
        entity.setApprovalStatus(ApprovalStatus.APPROVED);
        entity.setInputId(dataChangeDTO.getInputId());
        entity.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        entity.setInputDate(dataChangeDTO.getInputDate());
        entity.setApproveId(dataChangeDTO.getApproveId());
        entity.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        entity.setApproveDate(convertDateUtil.getDate());
    }
}
