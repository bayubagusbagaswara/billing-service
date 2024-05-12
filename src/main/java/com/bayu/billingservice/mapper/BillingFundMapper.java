package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BillingFundMapper {
    
    private final ModelMapper modelMapper;
    private final ConvertDateUtil convertDateUtil;

    public BillingFundMapper(ModelMapper modelMapper, ConvertDateUtil convertDateUtil) {
        this.modelMapper = modelMapper;
        this.convertDateUtil = convertDateUtil;
    }

    private void configureMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.addMappings(new PropertyMap<BillingFund, BillingFundDTO>() {
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

    public BillingFund mapFromDtoToEntity(BillingFundDTO billingFundDTO) {
        return modelMapper.map(billingFundDTO, BillingFund.class);
    }

    public BillingFundDTO mapFromEntityToDto(BillingFund billingFund) {
        return modelMapper.map(billingFund, BillingFundDTO.class);
    }

    public List<BillingFundDTO> mapToDTOList(List<BillingFund> billingFundList) {
        return billingFundList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public BillingFund createEntity(BillingFundDTO billingFundDTO, BillingDataChangeDTO dataChangeDTO) {
        BillingFund billingFund = modelMapper.map(billingFundDTO, BillingFund.class);
        billingFund.setApprovalStatus(ApprovalStatus.APPROVED);
        billingFund.setInputId(dataChangeDTO.getInputId());
        billingFund.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        billingFund.setInputDate(dataChangeDTO.getInputDate());
        billingFund.setApproveId(dataChangeDTO.getApproveId());
        billingFund.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        billingFund.setApproveDate(convertDateUtil.getDate());
        return billingFund;
    }

    public BillingFund updateEntity(BillingFund billingFundUpdated, BillingDataChangeDTO dataChangeDTO) {
        BillingFund billingFund = modelMapper.map(billingFundUpdated, BillingFund.class);
        billingFund.setApprovalStatus(ApprovalStatus.APPROVED);
        billingFund.setInputId(dataChangeDTO.getInputId());
        billingFund.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        billingFund.setInputDate(dataChangeDTO.getInputDate());
        billingFund.setApproveId(dataChangeDTO.getApproveId());
        billingFund.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        billingFund.setApproveDate(convertDateUtil.getDate());
        return billingFund;
    }

    public void mapObjects(BillingFundDTO billingFundDTOSource, BillingFund billingFundTarget) {
        modelMapper.map(billingFundDTOSource, billingFundTarget);
    }

}
