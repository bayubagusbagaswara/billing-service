package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.SkTransactionDTO;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.repository.SkTransactionRepository;
import com.bayu.billingservice.service.SkTransactionService;
import com.bayu.billingservice.util.CsvDataMapper;
import com.bayu.billingservice.util.CsvReaderUtil;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            List<String[]> rows = CsvReaderUtil.readCsvFile(filePath);

            List<SkTransaction> skTransactionList = CsvDataMapper.mapCsvSkTransaction(rows);

            skTransactionRepository.saveAll(skTransactionList);

            return "[SK Transaction] CSV data processed and saved successfully";
        } catch (IOException | CsvException e) {
            return "[SK Transaction] Failed to process CSV file: " + e.getMessage();
        }
    }

    @Override
    public List<SkTransaction> getAll() {
        return skTransactionRepository.findAll();
    }

    @Override
    public List<SkTransaction> getAllByPortfolioCode(String portfolioCode) {
        return skTransactionRepository.findAllByPortfolioCode(portfolioCode);
    }

    @Override
    public List<SkTransaction> getAllByPortfolioCodeAndSystem(String portfolioCode, String system) {
        return skTransactionRepository.findAllByPortfolioCodeAndSettlementSystem(portfolioCode, system);
    }

    @Override
    public List<SkTransactionDTO> getAllSettlementDate() {
        return mapToDTOList(skTransactionRepository.findAll());
    }

    @Override
    public List<SkTransaction> getAllByAidAndMonthAndYear(String aid, String month, Integer year) {
        log.info("Start get all SK TRAN by AID : {}, Month : {}, and Year : {}", aid, month, year);
        return skTransactionRepository.findAllByPortfolioCodeAndMonthAndYear(aid, month, year);
    }

    @Override
    public String deleteAll() {
        try {
            skTransactionRepository.deleteAll();
            return "Successfully deleted all SK TRAN";
        } catch (Exception e) {
            log.error("Error when delete all SK TRAN : " + e.getMessage());
            throw new ConnectionDatabaseException("Error when delete all SK TRAN");
        }
    }

    private static SkTransactionDTO mapToDTO(SkTransaction skTransaction) {
        return SkTransactionDTO.builder()
                .id(skTransaction.getId())
                .portfolioCode(skTransaction.getPortfolioCode())
                .tradeDate(skTransaction.getTradeDate())
                .settlementDate(skTransaction.getSettlementDate())
                .amount(skTransaction.getAmount())
                .settlementSystem(skTransaction.getSettlementSystem())
                .build();
    }

    private static List<SkTransactionDTO> mapToDTOList(List<SkTransaction> skTransactionList) {
        return skTransactionList.stream()
                .map(SkTransactionServiceImpl::mapToDTO)
                .toList();
    }

}
