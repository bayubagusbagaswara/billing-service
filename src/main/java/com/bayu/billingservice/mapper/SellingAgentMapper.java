package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.sellingagent.SellingAgentDTO;
import com.bayu.billingservice.model.SellingAgent;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

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
