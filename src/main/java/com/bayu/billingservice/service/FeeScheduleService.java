package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.*;

import java.math.BigDecimal;
import java.util.List;

public interface FeeScheduleService {

    FeeScheduleResponse createSingleData(CreateFeeScheduleRequest createFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO);

    FeeScheduleResponse createSingleApprove(FeeScheduleApproveRequest approveRequest);

    FeeScheduleResponse updateSingleData(UpdateFeeScheduleRequest updateFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO);

    FeeScheduleResponse updateMultipleData(FeeScheduleListRequest listRequest, BillingDataChangeDTO dataChangeDTO);

    FeeScheduleResponse updateSingleApprove(FeeScheduleApproveRequest approveRequest);

    FeeScheduleResponse deleteSingleData(DeleteFeeScheduleRequest deleteFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO);

    FeeScheduleResponse deleteSingleApprove(FeeScheduleApproveRequest approveRequest);

    BigDecimal checkFeeScheduleAndGetFeeValue(BigDecimal amount);

    List<FeeScheduleDTO> getAll();

    String deleteAll();
}
