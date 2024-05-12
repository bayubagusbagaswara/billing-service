package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.*;

import java.util.List;

public interface FeeScheduleService {
    CreateFeeScheduleListResponse createSingleData(CreateFeeScheduleRequest createFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO);

    CreateFeeScheduleListResponse createMultipleApprove(CreateFeeScheduleListRequest createFeeScheduleListRequest);

    UpdateFeeScheduleListResponse updateSingleData(UpdateFeeScheduleRequest updateFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateFeeScheduleListResponse updateMultipleData(UpdateFeeScheduleListRequest updateFeeScheduleListRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateFeeScheduleListResponse updateMultipleApprove(UpdateFeeScheduleListRequest updateFeeScheduleListRequest);

    DeleteFeeScheduleListResponse deleteSingleData(DeleteFeeScheduleRequest deleteFeeScheduleRequest, BillingDataChangeDTO dataChangeDTO);

    DeleteFeeScheduleListResponse deleteMultipleApprove(DeleteFeeScheduleListRequest deleteFeeScheduleListRequest);

    String deleteAll();

    List<FeeScheduleDTO> getAll();
}
