package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.dto.fund.FundCalculateRequest;
import com.bayu.billingservice.dto.pdf.GeneratePDFResponse;
import com.bayu.billingservice.exception.GeneratePDFBillingException;
import com.bayu.billingservice.mapper.BillingFundMapper;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.repository.BillingFundRepository;
import com.bayu.billingservice.service.FundGeneratePDFService;
import com.bayu.billingservice.util.ConvertDateUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bayu.billingservice.constant.FundConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundGeneratePDFServiceImpl implements FundGeneratePDFService {

    @Value("${base.path.billing.fund}")
    private String basePathBillingFund;

    @Value("${base.path.billing.image}")
    private String folderPathImage;

    private static final String ACCOUNT_BANK_BDI = "PT Bank Danamon Indonesia";
    private static final String DELIMITER = "/";

    private final BillingFundRepository billingFundRepository;
    private final SpringTemplateEngine templateEngine;
    private final PdfGenerator pdfGenerator;
    private final ConvertDateUtil convertDateUtil;
    private final BillingFundMapper fundMapper;

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

        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        List<BillingFundDTO> billingFundDTOList = fundMapper.mapToDTOList(billingFundList);

        for (BillingFundDTO fundDTO : billingFundDTOList) {
            try {
                log.info("Start generate PDF Billing Fund type '{}' and customer code '{}'", fundDTO.getBillingType(), fundDTO.getCustomerCode());

                String investmentManagementName = fundDTO.getInvestmentManagementName();
                String billingNumber = fundDTO.getBillingNumber();
                String customerCode = fundDTO.getCustomerCode();

                Map<String, String> monthYearMap = convertDateUtil.extractMonthYearInformation(fundDTO.getBillingPeriod());
                int year = Integer.parseInt(monthYearMap.get("year"));
                String yearMonthFormat = year + monthYearMap.get("monthValue");

                String fileName = generateFileName(billingNumber);
                String folderPath = basePathBillingFund + yearMonthFormat + DELIMITER + investmentManagementName;

                Path folderPathObj = Paths.get(folderPath);
                Files.createDirectories(folderPathObj);

                // Hapus file yang memiliki customerCode dalam nama file
                deleteFilesWithCustomerCode(folderPathObj, customerCode);

                String htmlContent = renderThymeleafTemplate(fundDTO);
                byte[] pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);
                savePdf(pdfBytes, folderPath, fileName);
                totalDataSuccess++;
            } catch (Exception e) {
                log.error("Error creating folder or saving PDF: {}", e.getMessage(), e);
                List<String> stringList = new ArrayList<>();
                stringList.add(e.getMessage());
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(fundDTO.getCustomerCode(), stringList);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            }
        }
        return new GeneratePDFResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
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

        String imageUrlHeader = "file:///" + folderPathImage + "/logo.png";
        String imageUrlFooter = "file:///" + folderPathImage + "/footer.png";
        context.setVariable("imageUrlHeader", imageUrlHeader);
        context.setVariable("imageUrlFooter", imageUrlFooter);

        return templateEngine.process(fundDTO.getBillingTemplate(), context);
    }

    private String generateFileName(String billingNumber) {
        log.info("Billing Number: {}", billingNumber);

        String replaceBillingNumber = billingNumber
                .replace("/", "_")
                .replace("-", "_");

        log.info("Replaced Billing Number: {}", replaceBillingNumber);

        String fileName = String.format("%s.pdf", replaceBillingNumber);
        log.info("Generated File Name: {}", fileName);

        return fileName;
    }

    private void savePdf(byte[] pdfBytes, String folderPath, String fileName) throws IOException {
        Path outputPathObj = Paths.get(folderPath).resolve(fileName);
        String outputPath = outputPathObj.toString();
        pdfGenerator.savePdfToFile(pdfBytes, outputPath);
    }

    private void deleteFilesWithCustomerCode(Path folderPathObj, String customerCode) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPathObj, "*.pdf")) {
            for (Path path : directoryStream) {
                if (path.getFileName().toString().contains(customerCode)) {
                    Files.delete(path);
                    log.info("Deleted file: {}", path.getFileName().toString());
                }
            }
        }
    }

}
