package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListRequest;
import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListResponse;
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

    @Override
    public CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        // TODO: 1. Validation request data

        // TODO: 2. Check code for make sure is not exist in table, because code is unique

        // TODO: 3. Create data change
        return null;
    }

    @Override
    public CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        return null;
    }
}
