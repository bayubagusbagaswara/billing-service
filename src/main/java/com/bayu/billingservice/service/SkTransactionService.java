package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.SkTransactionDTO;
import com.bayu.billingservice.model.SkTransaction;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.List;

public interface SkTransactionService {

    String readFileAndInsertToDB(String filePath) throws IOException, CsvException;
    List<SkTransaction> getAll();
    List<SkTransaction> getAllByPortfolioCode(String portfolioCode);
    List<SkTransaction> getAllByPortfolioCodeAndSystem(String portfolioCode, String system);

    List<SkTransactionDTO> getAllSettlementDate();
}
