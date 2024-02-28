package com.bayu.billingservice.service;

import com.bayu.billingservice.model.KseiSafekeepingFee;

import java.util.List;

public interface KseiSafekeepingFeeService {

    String readAndInsertToDB(String filePath);

    List<KseiSafekeepingFee> getAll();

    KseiSafekeepingFee getByFeeAccount(String feeAccount);

}
