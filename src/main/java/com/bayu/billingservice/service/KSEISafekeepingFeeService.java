package com.bayu.billingservice.service;

import com.bayu.billingservice.model.KSEISafekeepingFee;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.List;

public interface KSEISafekeepingFeeService {

    String readAndInsertToDB(String filePath);

    List<KSEISafekeepingFee> getAll();

    KSEISafekeepingFee getByFeeAccount(String feeAccount);

}
