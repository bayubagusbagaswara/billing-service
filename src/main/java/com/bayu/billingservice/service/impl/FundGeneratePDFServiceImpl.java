package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.GeneratePDFBillingException;
import com.bayu.billingservice.exception.UnexpectedException;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.BillingTemplate;
import com.bayu.billingservice.repository.BillingFundRepository;
import com.bayu.billingservice.service.FundGeneratePDFService;
import com.bayu.billingservice.util.ConvertBigDecimalUtil;
import com.bayu.billingservice.util.ConvertDateUtil;
import com.bayu.billingservice.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.bayu.billingservice.constant.FundConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundGeneratePDFServiceImpl implements FundGeneratePDFService {

    @Value("${base.path.billing.fund}")
    private String basePathBillingFund;

    @Value("${base.path.billing.image}")
    private String folderPathImage;

    private final BillingFundRepository billingFundRepository;
    private final SpringTemplateEngine templateEngine;
    private final PdfGenerator pdfGenerator;
    private final ConvertDateUtil convertDateUtil;

//    @Override
//    public List<BillingFundDTO> getAll() {
//        List<BillingFund> billingFundList = billingFundRepository.findAll();
//        return mapToDTOList(billingFundList);
//    }

    @Override
    public String generatePDF(String category, String monthYear) {
        try {
            log.info("Start generate PDF Billing Fund");

            String approvalStatus = ApprovalStatus.PENDING.getStatus();
            String month = "";
            Integer year = 1;

            List<BillingFund> billingFundList = billingFundRepository.findAllByBillingCategoryAndMonthAndYearAndApprovalStatus(
                    category, month, year, approvalStatus
            );

//            List<BillingFundDTO> fundDTOList = mapToDTOList(billingFundList);

//            generateAndSavePDFStatements(fundDTOList);

            log.info("Finished generate PDF Billing Fund");
            return "Successfully created a PDF file for Billing Fund";
        } catch (Exception e) {
            log.error("Error when generate PDF Billing Fund : " + e.getMessage(), e);
            throw new GeneratePDFBillingException("Error when generate PDF Billing Fund : " + e.getMessage());
        }
    }

    @Override
    public String deleteAll() {
        try {
            billingFundRepository.deleteAll();
            return "Successfully deleted all Billing Funds";
        } catch (Exception e) {
            log.error("Error when delete all Billing Funds : " + e.getMessage());
            throw new ConnectionDatabaseException("Error when delete all Billing Fund");
        }
    }

    private void generateAndSavePDFStatements(List<BillingFundDTO> fundDTOList) {
        for (BillingFundDTO fundDTO : fundDTOList) {
            Map<String, String> monthYearMap;
            String yearMonthFormat;
            String htmlContent;
            byte[] pdfBytes;
            String fileName;
            String folderPath;
            String outputPath;

            try {
                monthYearMap = convertDateUtil.extractMonthYearInformation(fundDTO.getBillingPeriod());
                yearMonthFormat = monthYearMap.get("year") + monthYearMap.get("monthValue");

                htmlContent = renderThymeleafTemplate(fundDTO);
                pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);
                fileName = generateFileName(fundDTO.getInvestmentManagementName(), fundDTO.getAid(), yearMonthFormat);

                folderPath = basePathBillingFund + yearMonthFormat;

                Path folderPathObj = Paths.get(folderPath);
                Files.createDirectories(folderPathObj);

                Path outputPathObj = folderPathObj.resolve(fileName);
                outputPath = outputPathObj.toString();

                pdfGenerator.savePdfToFile(pdfBytes, outputPath);
            } catch (IOException e) {
                log.error("Error creating folder or saving PDF : " + e.getMessage(), e);
                throw new GeneratePDFBillingException("Error creating folder or saving PDF : " + e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error : " + e.getMessage(), e);
                throw new UnexpectedException("Unexpected error when generate PDF file : " + e.getMessage());
            }
        }
    }

    private String renderThymeleafTemplate(BillingFundDTO fundDTO) {
        Context context = new Context();

        context.setVariable(BILLING_NUMBER, fundDTO.getBillingNumber());
        context.setVariable(BILLING_PERIOD, fundDTO.getBillingPeriod());
        context.setVariable(BILLING_STATEMENT_DATE, fundDTO.getBillingStatementDate());
        context.setVariable(BILLING_PAYMENT_DUE_DATE, fundDTO.getBillingPaymentDueDate());
        context.setVariable(BILLING_CATEGORY, fundDTO.getBillingCategory());
        context.setVariable(BILLING_TYPE, fundDTO.getBillingType());
        context.setVariable(BILLING_TEMPLATE, fundDTO.getBillingTemplate());
        context.setVariable(INVESTMENT_MANAGEMENT_NAME, fundDTO.getInvestmentManagementName());
        context.setVariable(INVESTMENT_MANAGEMENT_ADDRESS_BUILDING, fundDTO.getInvestmentManagementAddressBuilding());
        context.setVariable(INVESTMENT_MANAGEMENT_ADDRESS_STREET, fundDTO.getInvestmentManagementAddressStreet());
        context.setVariable(INVESTMENT_MANAGEMENT_ADDRESS_CITY, fundDTO.getInvestmentManagementAddressCity());
        context.setVariable(INVESTMENT_MANAGEMENT_ADDRESS_PROVINCE, fundDTO.getInvestmentManagementAddressProvince());
        context.setVariable(PRODUCT_NAME, fundDTO.getProductName());
        context.setVariable(ACCOUNT_NAME, fundDTO.getAccountName());
        context.setVariable(ACCOUNT_NUMBER, fundDTO.getAccountNumber());
        context.setVariable(ACCOUNT_BANK, fundDTO.getAccountBank());
        context.setVariable(CUSTOMER_FEE, fundDTO.getCustomerFee());
        context.setVariable(ACCRUAL_CUSTODIAL_FEE, fundDTO.getAccrualCustodialFee());
        context.setVariable(BI_SSSS_VALUE_FREQUENCY, fundDTO.getBis4ValueFrequency());
        context.setVariable(BI_SSSS_TRANSACTION_FEE, fundDTO.getBis4TransactionFee());
        context.setVariable(BI_SSSS_AMOUNT_DUE, fundDTO.getBis4AmountDue());
        context.setVariable(SUB_TOTAL, fundDTO.getSubTotal());
        context.setVariable(VAT_FEE, fundDTO.getVatFee());
        context.setVariable(VAT_AMOUNT_DUE, fundDTO.getVatAmountDue());
        context.setVariable(KSEI_VALUE_FREQUENCY, fundDTO.getKseiValueFrequency());
        context.setVariable(KSEI_TRANSACTION_FEE, fundDTO.getKseiTransactionFee());
        context.setVariable(TOTAL_AMOUNT_DUE, fundDTO.getTotalAmountDue());

        String imageUrlHeader = "file:///" + folderPathImage + "/danamon_header.png";
        String imageUrlFooter = "file:///" + folderPathImage + "/test.png";
        context.setVariable("imageUrlHeader", imageUrlHeader);
        context.setVariable("imageUrlFooter", imageUrlFooter);

        return templateEngine.process(BillingTemplate.FUND_TEMPLATE_1.getValue(), context);
    }

    private String generateFileName(String investmentManagementName, String aid, String yearMonth) {
        return investmentManagementName + "_" + aid + "_" + yearMonth + ".pdf";
    }

}
