package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.sellingagent.SellingAgentDTO;

public interface SellingAgentService {

    boolean isCodeAlreadyExists(String sellingAgentCode);

    SellingAgentDTO getBySellingAgentCode(String sellingAgentCode);
}
