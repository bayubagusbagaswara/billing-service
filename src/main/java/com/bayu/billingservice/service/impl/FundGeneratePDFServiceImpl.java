package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.ImageDTO;
import com.bayu.billingservice.dto.MonthYearDTO;
import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.dto.fund.FundCalculateRequest;
import com.bayu.billingservice.dto.pdf.GeneratePDFResponse;
import com.bayu.billingservice.dto.reportgenerator.CreateReportGeneratorRequest;
import com.bayu.billingservice.exception.GeneratePDFBillingException;
import com.bayu.billingservice.mapper.BillingFundMapper;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.BillingTemplate;
import com.bayu.billingservice.model.enumerator.ReportGeneratorStatus;
import com.bayu.billingservice.repository.BillingFundRepository;
import com.bayu.billingservice.service.FundGeneratePDFService;
import com.bayu.billingservice.service.ReportGeneratorService;
import com.bayu.billingservice.util.ConvertDateUtil;
import com.bayu.billingservice.util.ImageUtil;
import com.bayu.billingservice.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.bayu.billingservice.constant.FundConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundGeneratePDFServiceImpl implements FundGeneratePDFService {

    @Value("${base.path.billing.fund}")
    private String basePathBillingFund;

    private static final String ACCOUNT_BANK_BDI = "PT Bank Danamon Indonesia";
    private static final String DELIMITER = "/";

    private final BillingFundRepository billingFundRepository;
    private final SpringTemplateEngine templateEngine;
    private final PdfGenerator pdfGenerator;
    private final ConvertDateUtil convertDateUtil;
    private final BillingFundMapper fundMapper;
    private final ImageUtil imageUtil;
    private final ReportGeneratorService reportGeneratorService;

    @Override
    public String generatePDF(FundCalculateRequest fundCalculateRequest) {
        log.info("Start generate PDF Billing Fund with request: {}", fundCalculateRequest);
        try {
            String category = fundCalculateRequest.getCategory().toUpperCase();
            String monthYear = fundCalculateRequest.getMonthYear();
            String[] monthFormat = convertDateUtil.convertToYearMonthFormat(monthYear);
            String month = monthFormat[0];
            int year = Integer.parseInt(monthFormat[1]);

            String approvalStatus = ApprovalStatus.APPROVED.getStatus();

            List<BillingFund> billingFundList = billingFundRepository.findAllByBillingCategoryAndMonthAndYearAndApprovalStatus(
                    category, month, year, approvalStatus
            );

            GeneratePDFResponse generatePDFResponse = generateAndSavePdfStatements(billingFundList);

            return "Successfully created a PDF file for Billing Fund with total data success: " + generatePDFResponse.getTotalDataSuccess() + ", and total data failed: " + generatePDFResponse.getTotalDataFailed();
        } catch (Exception e) {
            log.error("Error when generate PDF Billing Fund: {}", e.getMessage(), e);
            throw new GeneratePDFBillingException("Error when generate PDF Billing Fund: " + e.getMessage());
        }
    }

    private GeneratePDFResponse generateAndSavePdfStatements(List<BillingFund> billingFundList) {
        log.info("Start generate and save pdf statements Billing Fund size: {}", billingFundList.size());
        Instant dateNow = Instant.now();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        List<BillingFundDTO> billingFundDTOList = fundMapper.mapToDTOList(billingFundList);

        for (BillingFundDTO fundDTO : billingFundDTOList) {
            MonthYearDTO monthYearDTO = convertDateUtil.parseBillingPeriodToLocalDate(fundDTO.getBillingPeriod());
            String yearMonthFormat = monthYearDTO.getYear() + monthYearDTO.getMonthValue();
            String fileName = generateFileName(fundDTO.getCustomerCode(), fundDTO.getSubCode(), fundDTO.getBillingNumber());

            /* get month and year */
            Integer year = monthYearDTO.getYear();
            String monthName = monthYearDTO.getMonthName();

            String folderPath = basePathBillingFund + yearMonthFormat + DELIMITER + fundDTO.getInvestmentManagementCode();
            String filePath = folderPath + DELIMITER + fileName;

            try {
                /* create folder to save pdf file */
                Path folderPathObj = Paths.get(folderPath);
                Files.createDirectories(folderPathObj);

                /* delete pdf files with customer code */
                deleteFilesWithCustomerCode(folderPathObj, fundDTO.getCustomerCode(), fundDTO.getSubCode());

                String htmlContent = renderThymeleafTemplate(fundDTO);
                byte[] pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);
                savePdf(pdfBytes, folderPath, fileName);

                /* check and delete existing report generator */
                reportGeneratorService.checkAndDeleteExisting(fundDTO.getCustomerCode(), fundDTO.getBillingCategory(),
                        fundDTO.getBillingType(), fundDTO.getCurrency(), fundDTO.getBillingPeriod());

                String description = "Successfully generate and save PDF statement with customer code: " + fundDTO.getCustomerCode();
                CreateReportGeneratorRequest createReportGeneratorRequest = new CreateReportGeneratorRequest(
                        dateNow, fundDTO.getInvestmentManagementCode(), fundDTO.getInvestmentManagementName(), fundDTO.getInvestmentManagementEmail(),
                        fundDTO.getInvestmentManagementUniqueKey(), fundDTO.getCustomerCode(), fundDTO.getCustomerName(), fundDTO.getBillingCategory(),
                        fundDTO.getBillingType(), fundDTO.getBillingPeriod(), monthName, year, fundDTO.getCurrency(),
                        fileName, filePath, ReportGeneratorStatus.SUCCESS.getStatus(), description
                );
                reportGeneratorService.save(createReportGeneratorRequest);
                totalDataSuccess++;
            } catch (Exception e) {
                log.error("Error creating folder or saving PDF: {}", e.getMessage(), e);
                /* create report generator for saving failed process */
                CreateReportGeneratorRequest createReportGeneratorRequest = new CreateReportGeneratorRequest(
                        dateNow, fundDTO.getInvestmentManagementCode(), fundDTO.getInvestmentManagementName(), fundDTO.getInvestmentManagementEmail(),
                        fundDTO.getInvestmentManagementUniqueKey(), fundDTO.getCustomerCode(), fundDTO.getCustomerName(), fundDTO.getBillingCategory(),
                        fundDTO.getBillingType(), fundDTO.getBillingPeriod(), monthName, year, fundDTO.getCurrency(),
                        fileName, folderPath, ReportGeneratorStatus.SUCCESS.getStatus(), e.getMessage()
                );
                reportGeneratorService.save(createReportGeneratorRequest);
                totalDataFailed++;
            }
        }
        return new GeneratePDFResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    private String renderThymeleafTemplate(BillingFundDTO fundDTO) {
        ImageDTO headerAndFooterImage = imageUtil.getHeaderAndFooterImage();
        Context context = new Context();

        context.setVariable(BILLING_NUMBER, fundDTO.getBillingNumber());
        context.setVariable(BILLING_PERIOD, fundDTO.getBillingPeriod());
        context.setVariable(BILLING_STATEMENT_DATE, fundDTO.getBillingStatementDate());
        context.setVariable(BILLING_PAYMENT_DUE_DATE, fundDTO.getBillingPaymentDueDate());
        context.setVariable(BILLING_CATEGORY, fundDTO.getBillingCategory());
        context.setVariable(BILLING_TYPE, fundDTO.getBillingType());
        context.setVariable(BILLING_TEMPLATE, fundDTO.getBillingTemplate());
        context.setVariable(INVESTMENT_MANAGEMENT_NAME, fundDTO.getInvestmentManagementName());
        context.setVariable(INVESTMENT_MANAGEMENT_ADDRESS_1, fundDTO.getInvestmentManagementAddress1());
        context.setVariable(INVESTMENT_MANAGEMENT_ADDRESS_2, fundDTO.getInvestmentManagementAddress2());
        context.setVariable(INVESTMENT_MANAGEMENT_ADDRESS_3, fundDTO.getInvestmentManagementAddress3());
        context.setVariable(INVESTMENT_MANAGEMENT_ADDRESS_4, fundDTO.getInvestmentManagementAddress4());
        context.setVariable(ACCOUNT_NAME, fundDTO.getAccountName());
        context.setVariable(ACCOUNT_NUMBER, fundDTO.getAccount());
        context.setVariable(ACCOUNT_BANK, ACCOUNT_BANK_BDI);
        context.setVariable(ACCRUAL_CUSTODIAL_VALUE_FREQUENCY, fundDTO.getAccrualCustodialValueFrequency());
        context.setVariable(ACCRUAL_CUSTODIAL_SAFEKEEPING_FEE, fundDTO.getAccrualCustodialSafekeepingFee());
        context.setVariable(ACCRUAL_CUSTODIAL_FEE, fundDTO.getAccrualCustodialFee());
        context.setVariable(BI_SSSS_TRANSACTION_VALUE_FREQUENCY, fundDTO.getBis4TransactionValueFrequency());
        context.setVariable(BI_SSSS_TRANSACTION_FEE, fundDTO.getBis4TransactionFee());
        context.setVariable(BI_SSSS_TRANSACTION_AMOUNT_DUE, fundDTO.getBis4TransactionAmountDue());
        context.setVariable(SUB_TOTAL, fundDTO.getSubTotal());
        context.setVariable(VAT_FEE, fundDTO.getVatFee());
        context.setVariable(VAT_AMOUNT_DUE, fundDTO.getVatAmountDue());
        context.setVariable(KSEI_TRANSACTION_VALUE_FREQUENCY, fundDTO.getKseiTransactionValueFrequency());
        context.setVariable(KSEI_TRANSACTION_FEE, fundDTO.getKseiTransactionFee());
        context.setVariable(KSEI_TRANSACTION_AMOUNT_DUE, fundDTO.getKseiTransactionAmountDue());
        context.setVariable(TOTAL_AMOUNT_DUE, fundDTO.getTotalAmountDue());
        context.setVariable(IMAGE_URL_HEADER, headerAndFooterImage.getImageUrlHeader());
        context.setVariable(IMAGE_URL_FOOTER, headerAndFooterImage.getImageUrlFooter());

        return templateEngine.process(BillingTemplate.FUND_TEMPLATE.getValue(), context);
    }

    private String generateFileName(String customerCode, String subCode, String billingNumber) {
        String fileName;
        String replaceBillingNumber = billingNumber
                .replace("/", "_")
                .replace("-", "_");

        if (subCode == null || subCode.isEmpty()) {
            fileName = customerCode + "_" + replaceBillingNumber + ".pdf";
        } else {
            fileName = customerCode + "_" + subCode + "_" + replaceBillingNumber + ".pdf";
        }
        return fileName;
    }
    private void savePdf(byte[] pdfBytes, String folderPath, String fileName) throws IOException {
        Path outputPathObj = Paths.get(folderPath).resolve(fileName);
        String outputPath = outputPathObj.toString();
        pdfGenerator.savePdfToFile(pdfBytes, outputPath);
    }

    private void deleteFilesWithCustomerCode(Path folderPathObj, String customerCode, String subCode) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPathObj, "*.pdf")) {
            for (Path path : directoryStream) {
                log.info("File Path Name: {}", path.getFileName());
                if (Files.isRegularFile(path)) {
                    handleFile(path, customerCode, subCode);
                }
            }
        }
    }

    private void handleFile(Path path, String customerCode, String subCode) throws IOException {
        String fileName = path.getFileName().toString();
        if (shouldDeleteFile(fileName, customerCode, subCode)) {
            Files.delete(path);
            log.info("Deleted path: {}, file: {}", path, fileName);
        }
    }

    private boolean shouldDeleteFile(String fileName, String customerCode, String subCode) {
        if (subCode == null || subCode.isEmpty()) {
            return fileName.contains(customerCode);
        }
        return fileName.contains(subCode);
    }

}
