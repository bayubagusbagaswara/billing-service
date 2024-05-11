package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementRequest;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.dto.investmentmanagement.UpdateInvestmentManagementRequest;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvestmentManagementMapper {

    private final ModelMapperUtil modelMapperUtil;
    private final ConvertDateUtil convertDateUtil;

    public InvestmentManagementMapper(ModelMapperUtil modelMapperUtil, ConvertDateUtil convertDateUtil) {
        this.modelMapperUtil = modelMapperUtil;
        this.convertDateUtil = convertDateUtil;
    }

    public InvestmentManagement mapFromDtoToEntity(InvestmentManagementDTO investmentManagementDTO) {
        InvestmentManagement investmentManagement = new InvestmentManagement();
        modelMapperUtil.mapObjects(investmentManagementDTO, investmentManagement);
        return investmentManagement;
    }

    public InvestmentManagementDTO mapFromEntityToDto(InvestmentManagement investmentManagement) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<InvestmentManagement, InvestmentManagementDTO>() {
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
        });

        return modelMapper.map(investmentManagement, InvestmentManagementDTO.class);
    }

    public List<InvestmentManagementDTO> mapToDTOList(List<InvestmentManagement> investmentManagementList) {
        return investmentManagementList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public InvestmentManagementDTO mapFromCreateRequestToDto(CreateInvestmentManagementRequest createInvestmentManagementRequest) {
        InvestmentManagementDTO investmentManagementDTO = new InvestmentManagementDTO();
        modelMapperUtil.mapObjects(createInvestmentManagementRequest, investmentManagementDTO);
        return investmentManagementDTO;
    }

    public InvestmentManagementDTO mapFromUpdateRequestToDto(UpdateInvestmentManagementRequest updateInvestmentManagementRequest) {
        InvestmentManagementDTO investmentManagementDTO = new InvestmentManagementDTO();
        modelMapperUtil.mapObjects(updateInvestmentManagementRequest, investmentManagementDTO);
        return investmentManagementDTO;
    }

    public InvestmentManagement createEntity(InvestmentManagementDTO investmentManagementDTO, BillingDataChangeDTO dataChangeDTO) {
        InvestmentManagement investmentManagement = new InvestmentManagement();
        modelMapperUtil.mapObjects(investmentManagementDTO, investmentManagement);
        investmentManagement.setApprovalStatus(ApprovalStatus.APPROVED);
        investmentManagement.setInputId(dataChangeDTO.getInputId());
        investmentManagement.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        investmentManagement.setInputDate(dataChangeDTO.getInputDate());
        investmentManagement.setApproveId(dataChangeDTO.getApproveId());
        investmentManagement.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        investmentManagement.setApproveDate(convertDateUtil.getDate());
        return investmentManagement;
    }

    public InvestmentManagement updateEntity(InvestmentManagement investmentManagementUpdated, BillingDataChangeDTO dataChangeDTO) {
        InvestmentManagement investmentManagement = new InvestmentManagement();
        modelMapperUtil.mapObjects(investmentManagementUpdated, investmentManagement);
        investmentManagement.setApprovalStatus(ApprovalStatus.APPROVED);
        investmentManagement.setInputId(dataChangeDTO.getInputId());
        investmentManagement.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        investmentManagement.setInputDate(dataChangeDTO.getInputDate());
        investmentManagement.setApproveId(dataChangeDTO.getApproveId());
        investmentManagement.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        investmentManagement.setApproveDate(convertDateUtil.getDate());
        return investmentManagement;
    }
}
