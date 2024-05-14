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
