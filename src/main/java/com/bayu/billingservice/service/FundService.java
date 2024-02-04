package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.BillingFundDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;

import java.util.List;

public interface FundService {

    // semua request list dari depan (Fee Report) harus otomatis ter-generate billing nya

    List<BillingFundDTO> generateBillingFund(List<FeeReportRequest> request, String date);
}
