package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.model.SfValCrowdfunding;
import com.bayu.billingservice.repository.SfValCrowdfundingRepository;
import com.bayu.billingservice.service.SfValCrowdfundingService;
import com.bayu.billingservice.util.CsvDataMapper;
import com.bayu.billingservice.util.CsvReaderUtil;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SfValCrowdfundingServiceImpl implements SfValCrowdfundingService {

    private final SfValCrowdfundingRepository sfValCrowdfundingRepository;

    @Override
    public String readAndInsertToDB(String filePath) {
        log.info("Start read and insert SfVal Crowd Funding to the database from file path : {}", filePath);
        try {
            List<String[]> rows = CsvReaderUtil.readCsvFile(filePath);

            List<SfValCrowdfunding> sfValCrowdfundingList = CsvDataMapper.mapCsvSfValCrowdFunding(rows);

            List<SfValCrowdfunding> sfValCrowdfundingListSaved = sfValCrowdfundingRepository.saveAll(sfValCrowdfundingList);

            return "[SfVal Crowd Funding] CSV data processed and saved successfully with total : " + sfValCrowdfundingListSaved.size();
        } catch (IOException | CsvException e) {
            log.error("Error when process CSV file SfVal Crowd Funding : " + e.getMessage(), e);
            return "[SfVal Crowd Funding] Failed to process CSV File : " + e.getMessage();
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
