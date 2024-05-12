package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.CreateFeeScheduleRequest;
import com.bayu.billingservice.dto.feeschedule.FeeScheduleDTO;
import com.bayu.billingservice.dto.feeschedule.UpdateFeeScheduleListRequest;
import com.bayu.billingservice.model.FeeSchedule;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeeScheduleMapper {

    private final ModelMapperUtil modelMapperUtil;
    private final ConvertDateUtil convertDateUtil;

    public FeeSchedule mapFromDtoToEntity(FeeScheduleDTO feeScheduleDTO) {
        FeeSchedule feeSchedule = new FeeSchedule();
        modelMapperUtil.mapObjects(feeScheduleDTO, feeSchedule);
        return feeSchedule;
    }

    public FeeScheduleDTO mapFromEntityToDto(FeeSchedule feeSchedule) {
        ModelMapper modelMapper = modelMapperUtil.getModelMapper();
        modelMapper.addMappings(new PropertyMap<FeeSchedule, FeeScheduleDTO>() {
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
        return modelMapper.map(feeSchedule, FeeScheduleDTO.class);
    }

    public List<FeeScheduleDTO> mapToDTOList(List<FeeSchedule> feeSchedules) {
        return feeSchedules.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public FeeScheduleDTO mapFromCreateFeeScheduleRequestToDto(CreateFeeScheduleRequest createFeeScheduleRequest) {
        FeeScheduleDTO feeScheduleDTO = new FeeScheduleDTO();
        modelMapperUtil.mapObjects(createFeeScheduleRequest, feeScheduleDTO);
        return feeScheduleDTO;
    }

    public FeeSchedule createEntity(FeeScheduleDTO feeScheduleDTO, BillingDataChangeDTO dataChangeDTO) {
        FeeSchedule feeSchedule = new FeeSchedule();
        modelMapperUtil.mapObjects(feeScheduleDTO, feeSchedule);
        feeSchedule.setApprovalStatus(ApprovalStatus.APPROVED);
        feeSchedule.setInputId(dataChangeDTO.getInputId());
        feeSchedule.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        feeSchedule.setInputDate(dataChangeDTO.getInputDate());
        feeSchedule.setApproveId(dataChangeDTO.getApproveId());
        feeSchedule.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        feeSchedule.setApproveDate(convertDateUtil.getDate());
        return feeSchedule;
    }

    public FeeSchedule updateEntity(FeeSchedule feeScheduleUpdated, BillingDataChangeDTO dataChangeDTO) {
        FeeSchedule feeSchedule = new FeeSchedule();
        modelMapperUtil.mapObjects(feeScheduleUpdated, feeSchedule);
        feeSchedule.setApprovalStatus(ApprovalStatus.APPROVED);
        feeSchedule.setInputId(dataChangeDTO.getInputId());
        feeSchedule.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        feeSchedule.setInputDate(dataChangeDTO.getInputDate());
        feeSchedule.setApproveId(dataChangeDTO.getApproveId());
        feeSchedule.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        feeSchedule.setApproveDate(convertDateUtil.getDate());
        return feeSchedule;
    }

    public FeeScheduleDTO mapFromUpdateRequestToDto(UpdateFeeScheduleListRequest updateFeeScheduleListRequest) {
        FeeScheduleDTO feeScheduleDTO = new FeeScheduleDTO();
        modelMapperUtil.mapObjects(updateFeeScheduleListRequest, feeScheduleDTO);
        return feeScheduleDTO;
    }

    public void mapObjects(FeeScheduleDTO feeScheduleDTOSource, FeeSchedule feeScheduleTarget) {
        modelMapperUtil.mapObjects(feeScheduleDTOSource, feeScheduleTarget);
    }

}
