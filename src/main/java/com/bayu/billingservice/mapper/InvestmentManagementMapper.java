package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;


@Component
public class InvestmentManagementMapper extends BaseMapper<InvestmentManagement, InvestmentManagementDTO> {

    private final ConvertDateUtil convertDateUtil;

    public InvestmentManagementMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        super(modelMapper);
        this.convertDateUtil = convertDateUtil;
    }

    @Override
    protected PropertyMap<InvestmentManagement, InvestmentManagementDTO> getPropertyMap() {
        return new PropertyMap<>() {
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
    protected Class<InvestmentManagement> getEntityClass() {
        return InvestmentManagement.class;
    }

    @Override
    protected Class<InvestmentManagementDTO> getDtoClass() {
        return InvestmentManagementDTO.class;
    }

    @Override
    protected void setCommonProperties(InvestmentManagement entity, BillingDataChangeDTO dataChangeDTO) {
        entity.setApprovalStatus(ApprovalStatus.APPROVED);
        entity.setInputId(dataChangeDTO.getInputId());
        entity.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        entity.setInputDate(dataChangeDTO.getInputDate());
        entity.setApproveId(dataChangeDTO.getApproveId());
        entity.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        entity.setApproveDate(convertDateUtil.getDate());
    }

}
