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

    private final ModelMapper modelMapper;
    private final ConvertDateUtil convertDateUtil;

    public InvestmentManagementMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        this.modelMapper = modelMapper;
        this.convertDateUtil = convertDateUtil;
        configureMapper();
    }

    private void configureMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);

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
    }

    public InvestmentManagement mapFromDtoToEntity(InvestmentManagementDTO investmentManagementDTO) {
        return modelMapper.map(investmentManagementDTO, InvestmentManagement.class);
    }

    public InvestmentManagementDTO mapFromEntityToDto(InvestmentManagement investmentManagement) {
        return modelMapper.map(investmentManagement, InvestmentManagementDTO.class);
    }

    public List<InvestmentManagementDTO> mapToDTOList(List<InvestmentManagement> investmentManagementList) {
        return investmentManagementList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public InvestmentManagementDTO mapFromCreateRequestToDto(CreateInvestmentManagementRequest createInvestmentManagementRequest) {
        return modelMapper.map(createInvestmentManagementRequest, InvestmentManagementDTO.class);
    }

    public InvestmentManagementDTO mapFromUpdateRequestToDto(UpdateInvestmentManagementRequest updateInvestmentManagementRequest) {
        return modelMapper.map(updateInvestmentManagementRequest, InvestmentManagementDTO.class);
    }

    public InvestmentManagement createEntity(InvestmentManagementDTO investmentManagementDTO, BillingDataChangeDTO dataChangeDTO) {
        InvestmentManagement investmentManagement = modelMapper.map(investmentManagementDTO, InvestmentManagement.class);
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
        InvestmentManagement investmentManagement = modelMapper.map(investmentManagementUpdated, InvestmentManagement.class);
        investmentManagement.setApprovalStatus(ApprovalStatus.APPROVED);
        investmentManagement.setInputId(dataChangeDTO.getInputId());
        investmentManagement.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        investmentManagement.setInputDate(dataChangeDTO.getInputDate());
        investmentManagement.setApproveId(dataChangeDTO.getApproveId());
        investmentManagement.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        investmentManagement.setApproveDate(convertDateUtil.getDate());
        return investmentManagement;
    }

    public void mapObjects(InvestmentManagementDTO investmentManagementDTOSource, InvestmentManagement investmentManagementTarget) {
        modelMapper.map(investmentManagementDTOSource, investmentManagementTarget);
    }

}
