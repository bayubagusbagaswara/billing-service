package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.core.CoreCalculateRequest;
import com.bayu.billingservice.repository.BillingCoreRepository;
import com.bayu.billingservice.service.*;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoreType5And6ServiceImpl implements CoreType5And6Service {

    private final BillingCoreRepository billingCoreRepository;
    private final CustomerService customerService;
    private final InvestmentManagementService investmentManagementService;
    private final FeeParameterService feeParameterService;
    private final SkTransactionService skTransactionService;
    private final KseiSafekeepingFeeService kseiSafekeepingFeeService;
    private final BillingNumberService billingNumberService;
    private final ConvertDateUtil convertDateUtil;


    @Override
    public String calculate(CoreCalculateRequest request) {
        return "";
    }
}
