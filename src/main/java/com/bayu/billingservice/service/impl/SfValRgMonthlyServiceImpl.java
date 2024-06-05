package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.exception.*;
import com.bayu.billingservice.model.SfValRgMonthly;
import com.bayu.billingservice.repository.SfValRgMonthlyRepository;
import com.bayu.billingservice.service.SfValRgMonthlyService;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class SfValRgMonthlyServiceImpl implements SfValRgMonthlyService {

    private static final String BASE_FILE_NAME = "RG_Monthly_";
    private final SfValRgMonthlyRepository sfValRgMonthlyRepository;
    private final ConvertDateUtil convertDateUtil;

    @Transactional
    @Override
    public String readFileAndInsertToDB(String filePath, String monthYear) {
        log.info("Start read and insert SfVal RG Monthly to the database : {}", filePath);
        try {
            Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
            String monthName = monthMinus1.get("monthName");
            String monthValue = monthMinus1.get("monthValue");
            int year = Integer.parseInt(monthMinus1.get("year"));

            String fileName = BASE_FILE_NAME + year + monthValue + ".csv";
            String filePathNew = filePath + fileName;
            log.info("File path new RG Monthly: {}", filePathNew);

            // Check if the file exists
            File file = new File(filePathNew);
            if (!file.exists()) {
                log.error("File not found: {}", filePathNew);
                throw new DataNotFoundException("RG Monthly file not found with path: " + filePathNew);
            }

            sfValRgMonthlyRepository.deleteByMonthAndYear(monthName, year);

            List<String[]> rows = CsvReaderUtil.readCsvFile(filePathNew);
            List<SfValRgMonthly> sfValRgMonthlyList = CsvDataMapper.mapCsvSfValRgMonthly(rows);
            sfValRgMonthlyRepository.saveAll(sfValRgMonthlyList);

            return "RG Monthly CSV data processed and saved successfully";
        } catch (DataNotFoundException e) {
            log.error("RG Monthly file not found: {}", e.getMessage(), e);
            throw new DataNotFoundException(e.getMessage());
        } catch (IOException | CsvException e) {
            log.error("RG Monthly failed to process CSV data from file: {}", filePath, e);
            throw new CsvProcessingException("SfVal RG Monthly failed to process CSV data: ", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing file: {}", filePath, e);
            throw new GeneralException("Unexpected error: ", e);
        }
    }

    @Override
    public List<SfValRgMonthly> getAll() {
        log.info("Get all SfVal RG Monthly");
        return sfValRgMonthlyRepository.findAll();
    }

    @Override
    public List<SfValRgMonthly> getAllByAid(String aid) {
        log.info("Get all SfVal RG Monthly by Aid : {}", aid);
        return sfValRgMonthlyRepository.findAllByAid(aid);
    }

    @Override
    public SfValRgMonthly getByAidAndSecurityName(String aid, String securityName) {
        log.info("Get SfVal RG Monthly by Aid : {} and Security Name : {}", aid, securityName);
        return sfValRgMonthlyRepository.findByAidAndSecurityName(aid, securityName)
                .orElseThrow(() -> new DataNotFoundException("SfVal RG Monthly not found with Aid : " + aid + " and Security Name : " + securityName));
    }

    @Transactional
    @Override
    public String deleteAll() {
        try {
            sfValRgMonthlyRepository.deleteAll();
            return "Successfully deleted all Sf Val RG Monthly data";
        } catch (Exception e) {
            log.error("Error when delete all Sf Val RG Monthly : {}", e.getMessage(), e);
            throw new ConnectionDatabaseException("Error when delete all Sf Val RG Monthly");
        }
    }

    @Override
    public List<SfValRgMonthly> getAllByCustomerCodeAndMonthAndYear(String customerCode, String month, Integer year) {
        return sfValRgMonthlyRepository.findAllByCustomerCodeAndMonthAndYear(customerCode, month, year);
    }

}
