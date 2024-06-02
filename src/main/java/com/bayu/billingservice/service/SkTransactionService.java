package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.sktransaction.SkTransactionDTO;
import com.bayu.billingservice.model.SkTransaction;

import java.util.List;

public interface SkTransactionService {

    String readFileAndInsertToDB(String filePath, String monthYear);
    List<SkTransaction> getAll();
    List<SkTransaction> getAllByPortfolioCode(String portfolioCode);
    List<SkTransaction> getAllByPortfolioCodeAndSystem(String portfolioCode, String system);

    List<SkTransactionDTO> getAllSettlementDate();

    List<SkTransaction> getAllByAidAndMonthAndYear(String aid, String month, Integer year);

    int[] filterTransactionsType(List<SkTransaction> skTransactionList);

    String deleteAll();
}
