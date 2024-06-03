package com.bayu.billingservice.service;


import com.bayu.billingservice.dto.fund.FundCalculateRequest;

public interface FundGeneratePDFService {

    String generatePDF(FundCalculateRequest request);

}
