package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.sellingagent.CreateSellingAgentRequest;
import com.bayu.billingservice.dto.sellingagent.SellingAgentDTO;
import com.bayu.billingservice.dto.sellingagent.UpdateSellingAgentListRequest;
import com.bayu.billingservice.model.SellingAgent;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SellingAgentMapper {

    private final ModelMapperUtil modelMapperUtil;
    private final ConvertDateUtil convertDateUtil;

    public SellingAgent mapFromDtoToEntity(SellingAgentDTO sellingAgentDTO) {
        SellingAgent sellingAgent = new SellingAgent();
        modelMapperUtil.mapObjects(sellingAgentDTO, sellingAgent);
        return sellingAgent;
    }

    public SellingAgentDTO mapFromEntityToDto(SellingAgent sellingAgent) {
        ModelMapper modelMapper = modelMapperUtil.getModelMapper();
        modelMapper.addMappings(new PropertyMap<SellingAgent, SellingAgentDTO>() {
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

        return modelMapper.map(sellingAgent, SellingAgentDTO.class);
    }

    public List<SellingAgentDTO> mapToDTOList(List<SellingAgent> sellingAgentList) {
        return sellingAgentList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public SellingAgentDTO mapFromCreateSellingAgentRequestToDTO(CreateSellingAgentRequest createSellingAgentRequest) {
        SellingAgentDTO sellingAgentDTO = new SellingAgentDTO();
        modelMapperUtil.mapObjects(createSellingAgentRequest, sellingAgentDTO);
        return sellingAgentDTO;
    }

    public SellingAgent createEntity(SellingAgentDTO sellingAgentDTO, BillingDataChangeDTO dataChangeDTO) {
        SellingAgent sellingAgent = new SellingAgent();
        modelMapperUtil.mapObjects(sellingAgentDTO, sellingAgent);
        sellingAgent.setApprovalStatus(ApprovalStatus.APPROVED);
        sellingAgent.setInputId(dataChangeDTO.getInputId());
        sellingAgent.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        sellingAgent.setInputDate(dataChangeDTO.getInputDate());
        sellingAgent.setApproveId(dataChangeDTO.getApproveId());
        sellingAgent.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        sellingAgent.setApproveDate(convertDateUtil.getDate());
        return sellingAgent;
    }

    public SellingAgent updateEntity(SellingAgent sellingAgentUpdated, BillingDataChangeDTO dataChangeDTO) {
        SellingAgent sellingAgent = new SellingAgent();
        modelMapperUtil.mapObjects(sellingAgentUpdated, sellingAgent);
        sellingAgent.setApprovalStatus(dataChangeDTO.getApprovalStatus());
        sellingAgent.setInputId(dataChangeDTO.getInputId());
        sellingAgent.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        sellingAgent.setInputDate(dataChangeDTO.getInputDate());
        sellingAgent.setApproveId(dataChangeDTO.getApproveId());
        sellingAgent.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        sellingAgent.setApproveDate(convertDateUtil.getDate());
        return sellingAgent;
    }

    public SellingAgentDTO mapFromUpdateRequestToDto(UpdateSellingAgentListRequest updateSellingAgentListRequest) {
        SellingAgentDTO sellingAgentDTO = new SellingAgentDTO();
        modelMapperUtil.mapObjects(updateSellingAgentListRequest, sellingAgentDTO);
        return sellingAgentDTO;
    }

    public void mapObjects(SellingAgentDTO sellingAgentDTOSource, SellingAgent sellingAgentTarget) {
        modelMapperUtil.mapObjects(sellingAgentDTOSource, sellingAgentTarget);
    }

}
