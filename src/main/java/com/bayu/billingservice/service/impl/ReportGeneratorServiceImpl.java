package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.reportgenerator.CreateReportGeneratorRequest;
import com.bayu.billingservice.model.ReportGenerator;
import com.bayu.billingservice.repository.ReportGeneratorRepository;
import com.bayu.billingservice.service.ReportGeneratorService;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    private final ReportGeneratorRepository reportGeneratorRepository;
    private final ConvertDateUtil convertDateUtil;

    @Override
    public ReportGenerator save(CreateReportGeneratorRequest request) {
        ReportGenerator reportGenerator = ReportGenerator.builder()
                .createdAt(request.getDateNow())
                .investmentManagementCode(request.getInvestmentManagementCode())
                .investmentManagementName(request.getInvestmentManagementName())
                .investmentManagementEmail(request.getInvestmentManagementEmail())
                .investmentManagementUniqueKey(request.getInvestmentManagementUniqueKey())
                .customerCode(request.getCustomerCode())
                .customerName(request.getCustomerName())
                .billingCategory(request.getBillingCategory())
                .billingType(request.getBillingType())
                .billingPeriod(request.getBillingPeriod())
                .month(request.getMonth())
                .year(request.getYear())
                .currency(request.getCurrency())
                .fileName(request.getFileName())
                .filePath(request.getFilePath())
                .status(request.getStatus())
                .description(request.getDescription())
                .build();
        return reportGeneratorRepository.save(reportGenerator);
    }

    @Override
    public void checkAndDeleteExisting(String customerCode, String billingCategory, String billingType, String currency, String period) {
        String[] monthFormat = convertDateUtil.convertToYearMonthFormat(period);
        String monthName = monthFormat[0];
        int year = Integer.parseInt(monthFormat[1]);

        List<ReportGenerator> reportGeneratorList = reportGeneratorRepository.findAllByCustomerCodeAndCategoryAndTypeAndCurrencyAndMonthAndYear(
                customerCode, billingCategory, billingType, currency, monthName, year
        );

        log.info("Existing Billing Report Generator: '{}'", reportGeneratorList.size());

        if (!reportGeneratorList.isEmpty()) {
            for (ReportGenerator reportGenerator : reportGeneratorList) {
                reportGeneratorRepository.delete(reportGenerator);
            }
        }
    }

}
