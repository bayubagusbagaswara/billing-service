package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.dto.core.Core1DTO;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;
import com.bayu.billingservice.exception.CalculateBillingException;
import com.bayu.billingservice.service.*;
import com.bayu.billingservice.util.ConvertDateUtil;
import com.bayu.billingservice.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Core1CalculateServiceImpl implements Core1CalculateService {

    private final KycCustomerService kycCustomerService;
    private final FeeParameterService feeParameterService;
    private final SkTransactionService skTransactionService;
    private final SfValRgDailyService sfValRgDailyService;


    @Override
    public String calculate(CoreCalculateRequest request) {
        log.info("Start calculate billing core with request : {}", request);
        try {
            String categoryUpperCase = request.getCategory().toUpperCase();
            String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());
            String[] monthFormat = ConvertDateUtil.convertToYearMonthFormat(request.getMonthYear());


            // TODO: Call service Kyc Customer
            List<KycCustomerDTO> kycCustomerDTOList = kycCustomerService.getByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

            // TODO: Get value


            return null;
        } catch (Exception e) {
            log.error("Error when calculate Billing Funds : " + e.getMessage(), e);
            throw new CalculateBillingException("Error when calculate Billing Funds : " + e.getMessage());
        }
    }

}
