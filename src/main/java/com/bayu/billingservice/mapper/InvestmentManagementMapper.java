package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

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
                skip().setInputId(null);
                skip().setInputIPAddress(null);
                skip().setInputDate(null);
                skip().setApproveId(null);
                skip().setApproveIPAddress(null);
                skip().setApproveDate(null);
            }
        };
    }

    @Override
    public InvestmentManagement mapToEntity(InvestmentManagementDTO dto) {
        return super.mapToEntity(dto);
    }

    @Override
    public InvestmentManagementDTO mapToDto(InvestmentManagement entity) {
        return super.mapToDto(entity);
    }

    @Override
    public List<InvestmentManagementDTO> mapToDTOList(List<InvestmentManagement> entityList) {
        return super.mapToDTOList(entityList);
    }

    @Override
    public InvestmentManagementDTO mapFromCreateRequestToDto(Object createRequest, Class<InvestmentManagementDTO> dtoClass) {
        return super.mapFromCreateRequestToDto(createRequest, dtoClass);
    }

    @Override
    public InvestmentManagementDTO mapFromUpdateRequestToDto(Object updateRequest, Class<InvestmentManagementDTO> dtoClass) {
        return super.mapFromUpdateRequestToDto(updateRequest, dtoClass);
    }

    @Override
    public InvestmentManagement createEntity(InvestmentManagementDTO dto, BillingDataChangeDTO dataChangeDTO) {
        return super.createEntity(dto, dataChangeDTO);
    }

    @Override
    public InvestmentManagement updateEntity(InvestmentManagement updatedEntity, Class<InvestmentManagementDTO> dto, BillingDataChangeDTO dataChangeDTO) {
        return super.updateEntity(updatedEntity, dto, dataChangeDTO);
    }

    @Override
    public void mapObjects(InvestmentManagementDTO sourceDto, InvestmentManagement targetEntity) {
        super.mapObjects(sourceDto, targetEntity);
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
