package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.fund.FeeReportRequest;

import java.util.List;

public interface FundCalculateService {

    String calculate(List<FeeReportRequest> request, String month, Integer year);

}
