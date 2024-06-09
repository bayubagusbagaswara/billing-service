package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.reportgenerator.CreateReportGeneratorRequest;
import com.bayu.billingservice.model.ReportGenerator;

public interface ReportGeneratorService {

    ReportGenerator save(CreateReportGeneratorRequest request);

    void checkAndDeleteExisting(String customerCode, String billingCategory, String billingType, String currency, String period);

}
