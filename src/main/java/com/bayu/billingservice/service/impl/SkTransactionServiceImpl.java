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
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkTransactionServiceImpl implements SkTransactionService {

    private final SkTransactionRepository skTransactionRepository;

    @Transactional
    @Override
    public String readFileAndInsertToDB(String filePath) throws IOException, CsvException {
        log.info("Start read and insert SkTransaction to the database : {}", filePath);
        try {
            List<String[]> rows = readCsvFile(filePath);

            List<SkTransaction> skTransactionList = CsvDataMapper.mapCsvSkTransaction(rows);

            for (SkTransaction skTransaction : skTransactionList) {
                skTransactionRepository.save(skTransaction);
            }

            return "CSV data processed and saved successfully";
        } catch (IOException | CsvException e) {
            return "Failed to process CSV file: " + e.getMessage();
        }
    }

    @Override
    public List<SkTransaction> getAllByPortfolioCode(String portfolioCode) {
        return skTransactionRepository.findAllByPortfolioCode(portfolioCode);
    }

    @Override
    public List<SkTransaction> getAllByPortfolioCodeAndSystem(String portfolioCode, String system) {
        return skTransactionRepository.findAllByPortfolioCodeAndSettlementSystem(portfolioCode, system);
    }

    private static List<String[]> readCsvFile(String filePath) throws IOException, CsvException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            return csvReader.readAll();
        }
    }
}
