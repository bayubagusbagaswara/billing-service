package com.bayu.billingservice.service;

import com.bayu.billingservice.model.KSEISafekeepingFee;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.List;

public interface KSEISafekeepingFeeService {

    List<KSEISafekeepingFee> readAndInsert(String filePath) throws IOException, CsvException;

    List<KSEISafekeepingFee> getAll();

    KSEISafekeepingFee getByFeeAccount();

}
