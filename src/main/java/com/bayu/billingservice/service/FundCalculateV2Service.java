package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.fund.FeeReportRequest;

import java.util.List;

public interface FundCalculateV2Service {

    BillingCalculationResponse calculate(List<FeeReportRequest> request, String monthYear);

}
