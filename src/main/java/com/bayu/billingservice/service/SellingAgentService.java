package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.sellingagent.*;

import java.util.List;

public interface SellingAgentService {

    boolean isCodeAlreadyExists(String sellingAgentCode);

    SellingAgentDTO getBySellingAgentCode(String sellingAgentCode);

    SellingAgentResponse createSingleData(CreateSellingAgentRequest createSellingAgentRequest, BillingDataChangeDTO dataChangeDTO);

    SellingAgentResponse createSingleApprove(SellingAgentApproveRequest sellingAgentApproveRequest, String clientIP);

    SellingAgentResponse updateSingleData(UpdateSellingAgentRequest updateSellingAgentRequest, BillingDataChangeDTO dataChangeDTO);

    SellingAgentResponse updateSingleApprove(SellingAgentApproveRequest sellingAgentApproveRequest, String clientIP);

    SellingAgentResponse deleteSingleData(DeleteSellingAgentRequest deleteSellingAgentRequest, BillingDataChangeDTO dataChangeDTO);

    SellingAgentResponse deleteSingleApprove(SellingAgentApproveRequest approveRequest, String clientIP);

    String deleteAll();

    List<SellingAgentDTO> getAll();
}
