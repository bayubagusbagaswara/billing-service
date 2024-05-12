package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.*;
import com.bayu.billingservice.repository.FeeScheduleRepository;
import com.bayu.billingservice.service.FeeScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeScheduleServiceImpl implements FeeScheduleService {

    private final FeeScheduleRepository feeScheduleRepository;

    @Override
    public CreateFeeScheduleListResponse createSingleData(CreateFeeScheduleRequest createFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public CreateFeeScheduleListResponse createMultipleApprove(CreateFeeScheduleListRequest createFeeScheduleListRequest) {
        return null;
    }

    @Override
    public UpdateFeeScheduleListResponse updateSingleData(UpdateFeeScheduleRequest updateFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateFeeScheduleListResponse updateMultipleData(UpdateFeeScheduleListRequest updateFeeScheduleListRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateFeeScheduleListResponse updateMultipleApprove(UpdateFeeScheduleListRequest updateFeeScheduleListRequest) {
        return null;
    }

    @Override
    public DeleteFeeScheduleListResponse deleteSingleData(DeleteFeeScheduleRequest deleteFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public DeleteFeeScheduleListResponse deleteMultipleApprove(DeleteFeeScheduleListRequest deleteFeeScheduleListRequest) {
        return null;
    }

    @Override
    public String deleteAll() {
        return "";
    }

    @Override
    public List<FeeScheduleDTO> getAll() {
        return List.of();
    }
}
