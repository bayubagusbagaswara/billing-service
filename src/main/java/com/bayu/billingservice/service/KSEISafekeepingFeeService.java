package com.bayu.billingservice.service;

import com.bayu.billingservice.model.KSEISafekeepingFee;

import java.util.List;

public interface KSEISafekeepingFeeService {

    String readAndInsertToDB(String filePath);

    List<KSEISafekeepingFee> getAll();

    KSEISafekeepingFee getByFeeAccount(String feeAccount);

}
