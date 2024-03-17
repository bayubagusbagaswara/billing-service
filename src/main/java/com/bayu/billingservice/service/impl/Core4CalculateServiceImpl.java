package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.kyc.KycCustomerDTO;
import com.bayu.billingservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class Core4CalculateServiceImpl implements Core4CalculateService {

    private final BillingCustomerService billingCustomerService;
    private final FeeParameterService feeParameterService;
    private final SkTransactionService skTransactionService;
    private final SfValRgDailyService sfValRgDailyService;
    private final KseiSafekeepingFeeService kseiSafekeepingFeeService;

    @Override
    public Map<String, List<Object>> calculate(String category, String type, String monthYear) {

        // KYC Customer List
        List<KycCustomerDTO> kycCustomerDTOList = billingCustomerService.getByBillingCategoryAndBillingType(category, type);
        return null;
    }

}
