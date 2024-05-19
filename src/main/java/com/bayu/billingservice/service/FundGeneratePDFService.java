package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.fund.BillingFundDTO;

import java.util.List;

public interface FundGeneratePDFService {

//    List<BillingFundDTO> getAll();

    String generatePDF(String category, String monthYear);

    String deleteAll();
}
