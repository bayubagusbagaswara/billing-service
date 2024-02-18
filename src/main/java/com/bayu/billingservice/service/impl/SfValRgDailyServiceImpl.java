package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.repository.SfValRgDailyRepository;
import com.bayu.billingservice.service.SfValRgDailyService;
import com.bayu.billingservice.util.CsvDataMapper;
import com.bayu.billingservice.util.CsvReaderUtil;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class SfValRgDailyServiceImpl implements SfValRgDailyService {

    private final SfValRgDailyRepository sfValRgDailyRepository;

    public SfValRgDailyServiceImpl(SfValRgDailyRepository sfValRgDailyRepository) {
        this.sfValRgDailyRepository = sfValRgDailyRepository;
    }

    @Override
    public String readFileAndInsertToDB(String filePath) {
        log.info("Start read and insert SfVal RG Daily to the database : {}", filePath);
        try {
            List<String[]> rows = CsvReaderUtil.readCsvFile(filePath);

            List<SfValRgDaily> sfValRgDailyList = CsvDataMapper.mapCsvSfValRgDaily(rows);

            sfValRgDailyRepository.saveAll(sfValRgDailyList);

            return "[SfVal RG Daily] CSV data processed and saved successfully";
        } catch (IOException | CsvException e) {
            return "[SfVal RG Daily] Failed to process CSV File : " + e.getMessage();
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
