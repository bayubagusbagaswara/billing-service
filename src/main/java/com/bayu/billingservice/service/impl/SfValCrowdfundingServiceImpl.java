package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.CsvProcessingException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.model.SfValCrowdfunding;
import com.bayu.billingservice.repository.SfValCrowdfundingRepository;
import com.bayu.billingservice.service.SfValCrowdfundingService;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SfValCrowdfundingServiceImpl implements SfValCrowdfundingService {

    private static final String BASE_FILE_NAME = "Urun_Dana_";

    private final SfValCrowdfundingRepository sfValCrowdfundingRepository;
    private final ConvertDateUtil convertDateUtil;

    @Transactional
    @Override
    public String readAndInsertToDB(String filePath, String monthYear) {
        log.info("Start read and insert SfVal Crowd Funding to the database from file path : {}", filePath);
        try {
            Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
            String monthName = monthMinus1.get("monthName");
            String monthValue = monthMinus1.get("monthValue");
            int year = Integer.parseInt(monthMinus1.get("year"));

            String fileName = BASE_FILE_NAME + year + monthValue + ".csv";
            String filePathNew = filePath + fileName;
            log.info("File path new Urun Dana: {}", filePathNew);

            // Check if the file exists
            File file = new File(filePathNew);
            if (!file.exists()) {
                log.error("File not found: {}", filePathNew);
                throw new DataNotFoundException("Urun Dana file not found with path: " + filePathNew);
            }

            sfValCrowdfundingRepository.deleteByMonthAndYear(monthName, year);

            List<String[]> rows = CsvReaderUtil.readCsvFile(filePathNew);
            List<SfValCrowdfunding> sfValCrowdfundingList = CsvDataMapper.mapCsvSfValCrowdFunding(rows);
            List<SfValCrowdfunding> sfValCrowdfundingListSaved = sfValCrowdfundingRepository.saveAll(sfValCrowdfundingList);

            return "Urun Dana CSV data processed and saved successfully with total : " + sfValCrowdfundingListSaved.size();
        } catch (DataNotFoundException e) {
            log.error("Urun Dana file not found: {}", e.getMessage(), e);
            throw new DataNotFoundException(e.getMessage());
        } catch (IOException | CsvException e) {
            log.error("Urun Dana failed to process CSV data from file: {}", filePath, e);
            throw new CsvProcessingException("Urun Dana failed to process CSV data: ", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing file: {}", filePath, e);
            throw new GeneralException("Unexpected error: ", e);
        }
    }

    @Override
    public List<SfValCrowdfunding> getAll() {
        return sfValCrowdfundingRepository.findAll();
    }

    @Override
    public List<SfValCrowdfunding> getAllByClientCode(String clientCode) {
        return sfValCrowdfundingRepository.findAllByClientCode(clientCode);
    }

    @Override
    public List<SfValCrowdfunding> getAllByClientCodeAndMonthAndYear(String clientCode, String monthName, Integer year) {
        return sfValCrowdfundingRepository.findAllByClientCodeAndMonthAndYear(clientCode, monthName, year);
    }

    @Override
    public String deleteAll() {
        try {
            sfValCrowdfundingRepository.deleteAll();
            return "Successfully deleted all SfVal Crowdfunding data";
        } catch (Exception e) {
            log.error("Error when delete all SfVal Crowdfunding : " + e.getMessage(), e);
            throw new ConnectionDatabaseException("Error when delete all SfVal Crowdfunding");
        }
    }

}
