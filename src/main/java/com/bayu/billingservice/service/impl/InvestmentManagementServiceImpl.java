package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.repository.InvestmentManagementRepository;
import com.bayu.billingservice.service.InvestmentManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentManagementServiceImpl implements InvestmentManagementService {

    private final InvestmentManagementRepository investmentManagementRepository;

    @Override
    public Boolean checkExistByCode(String code) {
        // TRUE means the data is in the table
        Boolean existedByCode = investmentManagementRepository.existsByCode(code);
        log.info("Existed by code: {}", existedByCode);
        return existedByCode;
    }

}
