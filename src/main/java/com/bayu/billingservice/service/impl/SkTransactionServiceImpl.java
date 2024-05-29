package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.SkTransactionDTO;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.CsvProcessingException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.repository.SkTransactionRepository;
import com.bayu.billingservice.service.SkTransactionService;
import com.bayu.billingservice.util.ConvertDateUtil;
import com.bayu.billingservice.util.CsvDataMapper;
import com.bayu.billingservice.util.CsvReaderUtil;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.bayu.billingservice.model.enumerator.SkTransactionType.TRANSACTION_BI_SSSS;
import static com.bayu.billingservice.model.enumerator.SkTransactionType.TRANSACTION_CBEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkTransactionServiceImpl implements SkTransactionService {

    private static final String BASE_FILE_NAME = "Sktrans_";
    private final SkTransactionRepository skTransactionRepository;
    private final ConvertDateUtil convertDateUtil;

    @Transactional
    @Override
    public String readFileAndInsertToDB(String filePath, String monthYear) {
        log.info("Start read and insert SkTransaction to the database : {}", filePath);
        try {
            Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
            String monthName = monthMinus1.get("monthName");
            String monthValue = monthMinus1.get("monthValue");
            int year = Integer.parseInt(monthMinus1.get("year"));

            String fileName = BASE_FILE_NAME + year + monthValue + ".csv";
            String filePathNew = filePath + fileName;
            log.info("File path new Sk Transaction: {}", filePathNew);

            // Check if the file exists
            File file = new File(filePathNew);
            if (!file.exists()) {
                log.error("File not found: {}", filePathNew);
                throw new DataNotFoundException("Sk Transaction file not found with path: " + filePathNew);
            }

            skTransactionRepository.deleteByMonthAndYear(monthName, year);

            List<String[]> rows = CsvReaderUtil.readCsvFile(filePathNew);
            List<SkTransaction> skTransactionList = CsvDataMapper.mapCsvSkTransaction(rows);
            skTransactionRepository.saveAll(skTransactionList);

            return "SK Transaction CSV data processed and saved successfully";
        }  catch (DataNotFoundException e) {
            log.error("Sk Transaction file not found: {}", e.getMessage(), e);
            throw new DataNotFoundException(e.getMessage());
        } catch (IOException | CsvException e) {
            log.error("Sk Transaction Failed to process CSV data from file: {}", filePath, e);
            throw new CsvProcessingException("SfVal RG Monthly failed to process CSV data: ", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing file: {}", filePath, e);
            throw new GeneralException("Unexpected error: ", e);
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
    public int[] filterTransactionsType(List<SkTransaction> skTransactionList) {
        int transactionCBESTTotal = 0;
        int transactionBIS4Total = 0;

        for (SkTransaction skTransaction : skTransactionList) {
            String settlementSystem = skTransaction.getSettlementSystem();
            if (settlementSystem != null) {
                if (TRANSACTION_CBEST.getValue().equalsIgnoreCase(settlementSystem)) {
                        transactionCBESTTotal++;
                } else if (TRANSACTION_BI_SSSS.getValue().equalsIgnoreCase(settlementSystem)) {
                        transactionBIS4Total++;
                }
            }
        }
        log.info("Total KSEI : {}", transactionCBESTTotal);log.info("Total BI-S4 : {}", transactionBIS4Total);
        return new int[] {transactionCBESTTotal, transactionBIS4Total};
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
