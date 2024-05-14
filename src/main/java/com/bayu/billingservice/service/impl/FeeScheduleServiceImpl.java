package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.*;
import com.bayu.billingservice.repository.FeeScheduleRepository;
import com.bayu.billingservice.service.FeeScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeScheduleServiceImpl implements FeeScheduleService {

    private final FeeScheduleRepository feeScheduleRepository;

    @Override
    public FeeScheduleResponse createSingleData(CreateFeeScheduleRequest createFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public FeeScheduleResponse createSingleApprove(FeeScheduleApproveRequest approveRequest) {
        return null;
    }

    @Override
    public FeeScheduleResponse updateSingleData(UpdateFeeScheduleRequest updateFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public FeeScheduleResponse updateMultipleData(FeeScheduleListRequest listRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public FeeScheduleResponse updateSingleApprove(FeeScheduleApproveRequest approveRequest) {
        return null;
    }

    @Override
    public FeeScheduleResponse deleteSingleData(DeleteFeeScheduleRequest deleteFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public FeeScheduleResponse deleteSingleApprove(FeeScheduleApproveRequest approveRequest) {
        return null;
    }

    @Override
    public BigDecimal checkFeeScheduleAndGetFeeValue(BigDecimal amount) {
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
