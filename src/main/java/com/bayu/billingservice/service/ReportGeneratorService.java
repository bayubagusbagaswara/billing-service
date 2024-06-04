package com.bayu.billingservice.service;

import com.bayu.billingservice.model.ReportGenerator;

public interface ReportGeneratorService {

    ReportGenerator save(ReportGenerator reportGenerator);

    void checkAndDeleteExisting(String customerCode, String billingCategory, String billingType, String currency, String period);

}
