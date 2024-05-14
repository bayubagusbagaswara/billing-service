package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.assettransfercustomer.AssetTransferCustomerDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.AssetTransferCustomer;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public AssetTransferCustomerDTO mapToDto(AssetTransferCustomer entity) {
        return super.mapToDto(entity);
    }

    @Override
    public AssetTransferCustomer mapToEntity(AssetTransferCustomerDTO dto) {
        return super.mapToEntity(dto);
    }

    @Override
    public List<AssetTransferCustomerDTO> mapToDTOList(List<AssetTransferCustomer> entityList) {
        return super.mapToDTOList(entityList);
    }

    @Override
    public AssetTransferCustomerDTO mapFromCreateRequestToDto(Object createRequest, Class<AssetTransferCustomerDTO> dtoClass) {
        return super.mapFromCreateRequestToDto(createRequest, dtoClass);
    }

    @Override
    public AssetTransferCustomer createEntity(AssetTransferCustomerDTO dto, BillingDataChangeDTO dataChangeDTO) {
        return super.createEntity(dto, dataChangeDTO);
    }

    @Override
    public AssetTransferCustomerDTO mapFromUpdateRequestToDto(Object updateRequest, Class<AssetTransferCustomerDTO> dtoClass) {
        return super.mapFromUpdateRequestToDto(updateRequest, dtoClass);
    }

    @Override
    public AssetTransferCustomer updateEntity(AssetTransferCustomer updatedEntity, Class<AssetTransferCustomerDTO> dto, BillingDataChangeDTO dataChangeDTO) {
        return super.updateEntity(updatedEntity, dto, dataChangeDTO);
    }

    @Override
    public void mapObjects(AssetTransferCustomerDTO sourceDto, AssetTransferCustomer targetEntity) {
        super.mapObjects(sourceDto, targetEntity);
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
