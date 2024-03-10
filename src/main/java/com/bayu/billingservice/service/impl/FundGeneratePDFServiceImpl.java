package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.repository.BillingFundRepository;
import com.bayu.billingservice.service.FundGeneratePDFService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundGeneratePDFServiceImpl implements FundGeneratePDFService {

    private final BillingFundRepository billingFundRepository;

    @Override
    public String generatePDF() {
        return null;
    }
}
