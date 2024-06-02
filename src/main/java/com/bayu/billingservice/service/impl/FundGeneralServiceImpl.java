package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.repository.BillingFundRepository;
import com.bayu.billingservice.service.FundGeneralService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundGeneralServiceImpl implements FundGeneralService {

    private final BillingFundRepository fundRepository;

    @Override
    public List<BillingFund> getAll() {
        return fundRepository.findAll();
    }
}
