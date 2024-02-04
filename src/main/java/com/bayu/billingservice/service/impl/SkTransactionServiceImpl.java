package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.repository.SkTransactionRepository;
import com.bayu.billingservice.service.SkTransactionService;
import com.bayu.billingservice.util.CsvDataMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkTransactionServiceImpl implements SkTransactionService {

    private final SkTransactionRepository skTransactionRepository;

    @Override
    public List<SkTransaction> readFileAndInsertToDB(String filePath) throws IOException, CsvException {
        log.info("Start read and insert SkTransaction to the database : {}", filePath);
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();

            List<SkTransaction> skTransactionList = CsvDataMapper.mapCsvSkTransaction(rows);

            return skTransactionRepository.saveAll(skTransactionList);
        }
    }

    @Override
    public List<SkTransaction> getAllByPortfolioCode(String portfolioCode) {
        return skTransactionRepository.findAllByPortfolioCode(portfolioCode);
    }

    @Override
    public List<SkTransaction> getAllByPortfolioCodeAndSystem(String portfolioCode, String system) {
        return skTransactionRepository.findAllByPortfolioCodeAndSystem(portfolioCode, system);
    }

}
