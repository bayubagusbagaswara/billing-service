package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.sellingagent.*;

import java.util.List;

public interface SellingAgentService {

    boolean isCodeAlreadyExists(String sellingAgentCode);

    SellingAgentDTO getBySellingAgentCode(String sellingAgentCode);

    CreateSellingAgentListResponse createSingleData(CreateSellingAgentRequest createSellingAgentRequest, BillingDataChangeDTO dataChangeDTO);

    CreateSellingAgentListResponse createMultipleApprove(CreateSellingAgentListRequest createSellingAgentListRequest);

    UpdateSellingAgentListResponse updateSingleData(UpdateSellingAgentRequest updateSellingAgentRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateSellingAgentListResponse updateMultipleData(UpdateSellingAgentListRequest updateSellingAgentListRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateSellingAgentListResponse updateMultipleApprove(UpdateSellingAgentListRequest updateSellingAgentListRequest);

    DeleteSellingAgentListResponse deleteSingleData(DeleteSellingAgentRequest deleteSellingAgentRequest, BillingDataChangeDTO dataChangeDTO);

    DeleteSellingAgentListResponse deleteMultipleApprove(DeleteSellingAgentListRequest deleteSellingAgentListRequest);

    String deleteAll();

    List<SellingAgentDTO> getAll();
}
