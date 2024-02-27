package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.core.Core1DTO;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;
import com.bayu.billingservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Core1ServiceImpl implements Core1Service {

    private final KycCustomerService kycCustomerService;
    private final FeeParameterService feeParameterService;
    private final SkTransactionService skTransactionService;
    private final SfValRgDailyService sfValRgDailyService;


    @Override
    public List<Core1DTO> calculate(String category, String type, String monthYear) {
        log.info("Start calculate billing for category : {}, type : {}, and month year : {}", category, type, monthYear);

        // TODO: Call service Kyc Customer
        KycCustomerDTO kycCustomerDTO = kycCustomerService.getByBillingCategoryAndBillingType(category, type);

        // TODO: Get value


        return null;
    }

}
