package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.sellingagent.*;

import java.util.List;

public interface SellingAgentService {

    boolean isCodeAlreadyExists(String sellingAgentCode);

    SellingAgentDTO getBySellingAgentCode(String sellingAgentCode);

    SellingAgentResponse createSingleData(CreateSellingAgentRequest createSellingAgentRequest, BillingDataChangeDTO dataChangeDTO);

    // create approve approve
    SellingAgentResponse createSingleApprove(SellingAgentApproveRequest sellingAgentApproveRequest);

    // update single data
    SellingAgentResponse updateSingleData(UpdateSellingAgentRequest updateSellingAgentRequest, BillingDataChangeDTO dataChangeDTO);

    // update multiple data
    SellingAgentResponse updateMultipleData(SellingAgentListRequest listRequest, BillingDataChangeDTO dataChangeDTO);

    // update single approve
    SellingAgentResponse updateSingleApprove(SellingAgentApproveRequest sellingAgentApproveRequest);

    SellingAgentResponse deleteSingleData(DeleteSellingAgentRequest deleteSellingAgentRequest, BillingDataChangeDTO dataChangeDTO);

    SellingAgentResponse deleteSingleApprove(SellingAgentApproveRequest approveRequest);

    String deleteAll();

    List<SellingAgentDTO> getAll();
}
