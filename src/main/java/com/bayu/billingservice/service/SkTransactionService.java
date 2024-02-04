package com.bayu.billingservice.service;

import com.bayu.billingservice.model.SkTransaction;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.List;

public interface SkTransactionService {

    List<SkTransaction> readFileAndInsertToDB(String filePath) throws IOException, CsvException;
    List<SkTransaction> getAllByPortfolioCode(String portfolioCode);
    List<SkTransaction> getAllByPortfolioCodeAndSystem(String portfolioCode, String system);

}
