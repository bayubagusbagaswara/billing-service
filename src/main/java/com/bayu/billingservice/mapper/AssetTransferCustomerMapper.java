package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.assettransfercustomer.AssetTransferCustomerDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.AssetTransferCustomer;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class AssetTransferCustomerMapper extends BaseMapper<AssetTransferCustomer, AssetTransferCustomerDTO> {

    private final ConvertDateUtil convertDateUtil;

    public AssetTransferCustomerMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        super(modelMapper);
        this.convertDateUtil = convertDateUtil;
    }

    @Override
    protected PropertyMap<AssetTransferCustomer, AssetTransferCustomerDTO> getPropertyMap() {
        return new PropertyMap<AssetTransferCustomer, AssetTransferCustomerDTO>() {
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
    protected Class<AssetTransferCustomer> getEntityClass() {
        return AssetTransferCustomer.class;
    }

    @Override
    protected Class<AssetTransferCustomerDTO> getDtoClass() {
        return AssetTransferCustomerDTO.class;
    }

    @Override
    protected void setCommonProperties(AssetTransferCustomer entity, BillingDataChangeDTO dataChangeDTO) {
        entity.setApprovalStatus(ApprovalStatus.APPROVED);
        entity.setInputId(dataChangeDTO.getInputId());
        entity.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        entity.setInputDate(dataChangeDTO.getInputDate());
        entity.setApproveId(dataChangeDTO.getApproveId());
        entity.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        entity.setApproveDate(convertDateUtil.getDate());
    }
}
