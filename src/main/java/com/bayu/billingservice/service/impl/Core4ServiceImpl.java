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
public class Core4ServiceImpl implements Core4Service {

    private final KycCustomerService kycCustomerService;
    private final FeeParameterService feeParameterService;
    private SkTransactionService skTransactionService;
    private SfValRgDailyService sfValRgDailyService;
    private KseiSafekeepingFeeService kseiSafekeepingFeeService;

    @Override
    public Map<String, List<Object>> calculate(String category, String type, String monthYear) {

        // KYC Customer List
        List<KycCustomerDTO> kycCustomerDTOList = kycCustomerService.getByBillingCategoryAndBillingType(category, type);
        return null;
    }

}
