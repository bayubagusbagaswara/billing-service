package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementRequest;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.InvestmentManagement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvestmentManagementMapper {

    private final ModelMapperUtil modelMapperUtil;

    public InvestmentManagementMapper(ModelMapperUtil modelMapperUtil) {
        this.modelMapperUtil = modelMapperUtil;
    }

    public InvestmentManagement mapFromDtoToEntity(InvestmentManagementDTO investmentManagementDTO) {
        InvestmentManagement investmentManagement = new InvestmentManagement();
        modelMapperUtil.mapObjects(investmentManagementDTO, investmentManagement);
        return investmentManagement;
    }

    public InvestmentManagementDTO mapFromEntityToDto(InvestmentManagement investmentManagement) {
        InvestmentManagementDTO investmentManagementDTO = new InvestmentManagementDTO();
        modelMapperUtil.mapObjects(investmentManagement, investmentManagementDTO);
        return investmentManagementDTO;
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

    public InvestmentManagement createEntity(InvestmentManagementDTO investmentManagementDTO, BillingDataChangeDTO dataChangeDTO) {
        InvestmentManagement investmentManagement = new InvestmentManagement();
        modelMapperUtil.mapObjects(investmentManagementDTO, investmentManagement);
        investmentManagement.setApprovalStatus(dataChangeDTO.getApprovalStatus());
        investmentManagement.setInputId(dataChangeDTO.getInputId());
        investmentManagement.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        investmentManagement.setInputDate(dataChangeDTO.getInputDate());
        investmentManagement.setApprovalId(dataChangeDTO.getApproveId());
        investmentManagement.setApprovalIPAddress(dataChangeDTO.getApproveIPAddress());
        investmentManagement.setApprovalDate(dataChangeDTO.getApproveDate());
        return investmentManagement;
    }

}
