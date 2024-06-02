package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.CsvProcessingException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.repository.SfValRgDailyRepository;
import com.bayu.billingservice.service.SfValRgDailyService;
import com.bayu.billingservice.util.ConvertDateUtil;
import com.bayu.billingservice.util.CsvDataMapper;
import com.bayu.billingservice.util.CsvReaderUtil;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SfValRgDailyServiceImpl implements SfValRgDailyService {

    private static final String BASE_FILE_NAME = "RG_Daily_";

    private final SfValRgDailyRepository sfValRgDailyRepository;
    private final ConvertDateUtil convertDateUtil;

    @Override
    public String readFileAndInsertToDB(String filePath, String monthYear) {
        log.info("Start read and insert SfVal RG Daily to the database : {}", filePath);
        try {
            Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
            String monthName = monthMinus1.get("monthName");
            String monthValue = monthMinus1.get("monthValue");
            int year = Integer.parseInt(monthMinus1.get("year"));

            String fileName = BASE_FILE_NAME + year + monthValue + ".csv";
            String filePathNew = filePath + fileName;
            log.info("File path new RG Daily: {}", filePathNew);

            // Check if the file exists
            File file = new File(filePathNew);
            if (!file.exists()) {
                log.error("File not found: {}", filePathNew);
                throw new DataNotFoundException("RG Daily file not found with path: " + filePathNew);
            }

            sfValRgDailyRepository.deleteByMonthAndYear(monthName, year);

            List<String[]> rows = CsvReaderUtil.readCsvFile(filePathNew);
            List<SfValRgDaily> sfValRgDailyList = CsvDataMapper.mapCsvSfValRgDaily(rows);
            sfValRgDailyRepository.saveAll(sfValRgDailyList);

            return "RG Daily CSV data processed and saved successfully";
        } catch (DataNotFoundException e) {
            log.error("RG Daily file not found: {}", e.getMessage(), e);
            throw new DataNotFoundException(e.getMessage());
        } catch (IOException | CsvException e) {
            log.error("RG Daily failed to process CSV data from file: {}", filePath, e);
            throw new CsvProcessingException("SfVal RG Daily failed to process CSV data: ", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing file: {}", filePath, e);
            throw new GeneralException("Unexpected error: ", e);
        }
    }

    @Override
    public List<SfValRgDaily> getAll() {
        log.info("Get all Sf Val Rg Daily");
        return sfValRgDailyRepository.findAll();
    }

    @Override
    public List<SfValRgDaily> getAllByAid(String aid) {
        log.info("Get all Sf Val Rg Daily by Aid : {}", aid);
        return sfValRgDailyRepository.findAllByAid(aid);
    }

    @Override
    public List<SfValRgDaily> getAllByAidAndDate(String aid, LocalDate date) {
        log.info("Get all Sf Val Rg Daily by Aid : {} and Date : {}", aid, date);
        return sfValRgDailyRepository.findAllByAidAndDate(aid, date);
    }

    @Override
    public List<SfValRgDaily> getAllByAidAndMonthAndYear(String aid, String month, Integer year) {
        log.info("Get all Sf Val Rg Daily by Aid : {}, Month : {}, and Year : {}", aid, month, year);
        return sfValRgDailyRepository.findAllByAidAndYearAndMonth(aid, year, month);
    }

    @Override
    public List<SfValRgDaily> getAllByAidAndSecurityName(String aid, String securityName) {
        log.info("Get all Sf Val Rg Daily by Aid : {} and Security Name : {}", aid, securityName);
        return sfValRgDailyRepository.findAllByAidAndSecurityName(aid, securityName);
    }

    @Override
    public String deleteAll() {
        try {
            sfValRgDailyRepository.deleteAll();
            return "Successfully deleted all Sf Val RG Daily data";
        } catch (Exception e) {
            log.error("Error when delete all Sf Val RG Daily : " + e.getMessage(), e);
            throw new ConnectionDatabaseException("Error when delete all Sf Val RG Daily");
        }
    }

}
