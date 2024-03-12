package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.dto.core.Core1DTO;
import com.bayu.billingservice.exception.GeneratePDFBillingException;
import com.bayu.billingservice.exception.UnexpectedException;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.repository.BillingCoreRepository;
import com.bayu.billingservice.service.Core1GeneratePDFService;
import com.bayu.billingservice.util.ConvertBigDecimalUtil;
import com.bayu.billingservice.util.ConvertDateUtil;
import com.bayu.billingservice.util.PdfGenerator;
import com.bayu.billingservice.util.StringUtil;
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

import static com.bayu.billingservice.constant.CoreConstant.*;
import static com.bayu.billingservice.model.enumerator.ApprovalStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Core1GeneratePDFServiceImpl implements Core1GeneratePDFService {

    @Value("${base.path.billing.core}")
    private String basePathBillingCore;

    private final BillingCoreRepository billingCoreRepository;
    private final SpringTemplateEngine templateEngine;
    private final PdfGenerator pdfGenerator;

    @Override
    public List<Core1DTO> getAll() {
        List<BillingCore> billingCoreList = billingCoreRepository.findAll();
        return mapToDTOList(billingCoreList);
    }

    @Override
    public String generatePDF(CoreCalculateRequest request) {
        try {
            log.info("Start generate PDF Billing Core type 1");
            String categoryUpperCase = request.getCategory().toUpperCase();
            String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());
            String[] monthFormat = ConvertDateUtil.convertToYearMonthFormat(request.getMonthYear());
            String monthName = monthFormat[0];
            int year = Integer.parseInt(monthFormat[1]);

            String approvalStatus = PENDING.getStatus();

            List<BillingCore> billingCoreList = billingCoreRepository.findAllByBillingCategoryAndBillingTypeAndMonthAndYearAndApprovalStatus(
                    categoryUpperCase, typeUpperCase, monthName, year, approvalStatus
            );

            List<Core1DTO> core1DTOList = mapToDTOList(billingCoreList);

            generateAndSavePdfStatements(core1DTOList);

            log.info("Finished generate PDF Billing Core type 1");
            return "Successfully created a PDF file for Billing Core type 1";
        } catch (Exception e) {
            log.error("Error when generate PDF Billing Core type 1 : " + e.getMessage(), e);
            throw new GeneratePDFBillingException("Error when generate PDF Billing Core type 1 : " + e.getMessage());
        }
    }

    private void generateAndSavePdfStatements(List<Core1DTO> core1DTOList) {
        for (Core1DTO core1DTO : core1DTOList) {
            Map<String, String> monthYearMap;
            String yearMonthFormat;
            String htmlContent;
            byte[] pdfBytes;
            String fileName;
            String folderPath;
            String outputPath;

            try {
                monthYearMap = ConvertDateUtil.extractMonthYearInformation(core1DTO.getBillingPeriod());
                yearMonthFormat = monthYearMap.get("year") + monthYearMap.get("monthValue");

                htmlContent = renderThymeleafTemplate(core1DTO);
                pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);
                fileName = generateFileName(core1DTO.getAid(), yearMonthFormat);

                folderPath = basePathBillingCore + yearMonthFormat;

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

    private String renderThymeleafTemplate(Core1DTO core1DTO) {
        Context context = new Context();

        context.setVariable(BILLING_NUMBER, core1DTO.getBillingNumber());
        context.setVariable(BILLING_PERIOD, core1DTO.getBillingPeriod());
        context.setVariable(BILLING_STATEMENT_DATE, core1DTO.getBillingStatementDate());
        context.setVariable(BILLING_PAYMENT_DUE_DATE, core1DTO.getBillingPaymentDueDate());
        context.setVariable(BILLING_CATEGORY, core1DTO.getBillingCategory());
        context.setVariable(BILLING_TYPE, core1DTO.getBillingType());
        context.setVariable(BILLING_TEMPLATE, core1DTO.getBillingTemplate());
        context.setVariable(INVESTMENT_MANAGEMENT_NAME, core1DTO.getInvestmentManagementName());
        context.setVariable(INVESTMENT_MANAGEMENT_ADDRESS, core1DTO.getInvestmentManagementAddress());
        context.setVariable(PRODUCT_NAME, core1DTO.getProductName());
        context.setVariable(ACCOUNT_NAME, core1DTO.getAccountName());
        context.setVariable(ACCOUNT_NUMBER, core1DTO.getAccountNumber());
        context.setVariable(ACCOUNT_BANK, core1DTO.getAccountBank());

        context.setVariable(TRANSACTION_HANDLING_VALUE_FREQUENCY, core1DTO.getTransactionHandlingValueFrequency());
        context.setVariable(TRANSACTION_HANDLING_FEE, core1DTO.getTransactionHandlingFee());
        context.setVariable(TRANSACTION_HANDLING_AMOUNT_DUE, core1DTO.getTransactionHandlingAmountDue());
        context.setVariable(SAFEKEEPING_VALUE_FREQUENCY, core1DTO.getSafekeepingValueFrequency());
        context.setVariable(SAFEKEEPING_FEE, core1DTO.getSafekeepingFee());
        context.setVariable(SAFEKEEPING_AMOUNT_DUE, core1DTO.getSafekeepingAmountDue());
        context.setVariable(SUB_TOTAL, core1DTO.getSubTotal());
        context.setVariable(VAT_FEE, core1DTO.getVatFee());
        context.setVariable(VAT_AMOUNT_DUE, core1DTO.getVatAmountDue());
        context.setVariable(TOTAL_AMOUNT_DUE, core1DTO.getTotalAmountDue());

        String billingTemplate = core1DTO.getBillingTemplate();
        log.info("[Core type 1] Billing Template '{}'", billingTemplate);
        return templateEngine.process(billingTemplate, context);
    }

    private String generateFileName(String aid, String yearMonthFormat) {
        return aid + "_" + yearMonthFormat + ".pdf";
    }

    private static Core1DTO mapToDTO(BillingCore billingCore) {
        return Core1DTO.builder()
                .createdAt(billingCore.getCreatedAt())
                .updatedAt(billingCore.getUpdatedAt())
                .approvalStatus(billingCore.getApprovalStatus())
                .aid(billingCore.getAid())
                .month(billingCore.getMonth())
                .year(String.valueOf(billingCore.getYear()))
                .billingNumber(billingCore.getBillingNumber())
                .billingPeriod(billingCore.getBillingPeriod())
                .billingStatementDate(billingCore.getBillingStatementDate())
                .billingPaymentDueDate(billingCore.getBillingPaymentDueDate())
                .billingCategory(billingCore.getBillingCategory())
                .billingType(billingCore.getBillingType())
                .billingTemplate(billingCore.getBillingTemplate())
                .investmentManagementName(billingCore.getInvestmentManagementName())
                .investmentManagementAddress(billingCore.getInvestmentManagementAddress())
                .productName(billingCore.getProductName())
                .accountName(billingCore.getAccountName())
                .accountNumber(billingCore.getAccountNumber())
                .accountBank(billingCore.getAccountBank())
                .transactionHandlingValueFrequency(String.valueOf(billingCore.getTransactionHandlingValueFrequency()))
                .transactionHandlingFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingCore.getTransactionHandlingFee()))
                .transactionHandlingAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingCore.getTransactionHandlingAmountDue()))
                .safekeepingValueFrequency(ConvertBigDecimalUtil.formattedBigDecimalToString(billingCore.getSafekeepingValueFrequency()))
                .safekeepingFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingCore.getSafekeepingFee()))
                .safekeepingAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingCore.getSafekeepingAmountDue()))
                .subTotal(ConvertBigDecimalUtil.formattedBigDecimalToString(billingCore.getSubTotal()))
                .vatFee(ConvertBigDecimalUtil.formattedVatFee(billingCore.getVatFee()))
                .vatAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingCore.getVatAmountDue()))
                .totalAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingCore.getTotalAmountDue()))
                .build();
    }

    private static List<Core1DTO> mapToDTOList(List<BillingCore> billingCoreList) {
        return billingCoreList.stream()
                .map(Core1GeneratePDFServiceImpl::mapToDTO)
                .toList();
    }
}
