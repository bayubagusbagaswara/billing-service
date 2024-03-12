package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.SfValRgMonthly;
import com.bayu.billingservice.repository.SfValRgMonthlyRepository;
import com.bayu.billingservice.service.SfValRgMonthlyService;
import com.bayu.billingservice.util.CsvDataMapper;
import com.bayu.billingservice.util.CsvReaderUtil;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class SfValRgMonthlyServiceImpl implements SfValRgMonthlyService {

    private final SfValRgMonthlyRepository sfValRgMonthlyRepository;

    public SfValRgMonthlyServiceImpl(SfValRgMonthlyRepository sfValRgMonthlyRepository) {
        this.sfValRgMonthlyRepository = sfValRgMonthlyRepository;
    }

    @Override
    public String readFileAndInsertToDB(String filePath) {
        log.info("Start read and insert SfVal RG Monthly to the database : {}", filePath);

        try {
            List<String[]> rows = CsvReaderUtil.readCsvFile(filePath);

            List<SfValRgMonthly> sfValRgMonthlyList = CsvDataMapper.mapCsvSfValRgMonthly(rows);

            sfValRgMonthlyRepository.saveAll(sfValRgMonthlyList);

            return "[SfVal RG Monthly] CSV data processed and saved successfully";
        } catch (IOException | CsvException e) {
            return "[SfVal RG Monthly] Failed to process CSV File : " + e.getMessage();
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

    @Override
    public String deleteAll() {
        try {
            sfValRgMonthlyRepository.deleteAll();
            return "Successfully deleted all Sf Val RG Monthly data";
        } catch (Exception e) {
            log.error("Error when delete all Sf Val RG Monthly : " + e.getMessage(), e);
            throw new ConnectionDatabaseException("Error when delete all Sf Val RG Monthly");
        }
    }

}
