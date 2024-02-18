package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.core.Core1DTO;
import com.bayu.billingservice.service.Core1Service;
import com.bayu.billingservice.service.KycService;
import com.bayu.billingservice.service.SfValRgDailyService;
import com.bayu.billingservice.service.SkTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class Core1ServiceImpl implements Core1Service {

    private final KycService kycService;
    private final SkTransactionService skTransactionService;
    private final SfValRgDailyService sfValRgDailyService;

    public Core1ServiceImpl(KycService kycService, SkTransactionService skTransactionService, SfValRgDailyService sfValRgDailyService) {
        this.kycService = kycService;
        this.skTransactionService = skTransactionService;
        this.sfValRgDailyService = sfValRgDailyService;
    }

    @Override
    public List<Core1DTO> calculate(String category, String type, String monthYear) {
        log.info("Start calculate billing for category : {}, type : {}, and month year : {}", category, type, monthYear);



        return null;
    }

    @Override
    public String calculate1(String category, String type, String monthYear) {
        return null;
    }
}
