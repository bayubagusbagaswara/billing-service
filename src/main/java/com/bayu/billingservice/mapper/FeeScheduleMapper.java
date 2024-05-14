package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.FeeScheduleDTO;
import com.bayu.billingservice.model.FeeSchedule;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

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
    protected Class<FeeSchedule> getEntityClass() {
        return FeeSchedule.class;
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
