package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;

import java.util.List;

public interface FundService {

    List<BillingFundDTO> calculate(List<FeeReportRequest> request, String month, int year);

}
