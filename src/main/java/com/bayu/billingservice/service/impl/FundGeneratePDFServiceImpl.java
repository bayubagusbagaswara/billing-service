package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.repository.BillingFundRepository;
import com.bayu.billingservice.service.FundGeneratePDFService;
import com.bayu.billingservice.util.ConvertBigDecimalUtil;
import com.bayu.billingservice.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundGeneratePDFServiceImpl implements FundGeneratePDFService {

    @Value("${base.path.billing.fund}")
    private String basePathBillingFund;

    private final BillingFundRepository billingFundRepository;
    private final SpringTemplateEngine templateEngine;
    private final PdfGenerator pdfGenerator;

    @Override
    public List<BillingFundDTO> getAll() {
        List<BillingFund> billingFundList = billingFundRepository.findAll();
        return mapToDTOList(billingFundList);
    }

    @Override
    public String generatePDF(String category, String month, int year) {
        String approvalStatus = ApprovalStatus.PENDING.getStatus();

        List<BillingFund> billingFundList = billingFundRepository.findAllByBillingCategoryAndMonthAndYearAndApprovalStatus(
                category, month, year, approvalStatus
        );




        return null;
    }

    private static BillingFundDTO mapToDTO(BillingFund billingFund) {
        return BillingFundDTO.builder()
                .createdAt(billingFund.getCreatedAt())
                .updatedAt(billingFund.getUpdatedAt())
                .approvalStatus(billingFund.getApprovalStatus())
                .portfolioCode(billingFund.getPortfolioCode())
                .month(billingFund.getMonth())
                .year(String.valueOf(billingFund.getYear()))
                .billingNumber(billingFund.getBillingNumber())
                .billingPeriod(billingFund.getBillingPeriod())
                .billingStatementDate(billingFund.getBillingStatementDate())
                .billingPaymentDueDate(billingFund.getBillingPaymentDueDate())
                .billingCategory(billingFund.getBillingCategory())
                .billingType(billingFund.getBillingType())
                .billingTemplate(billingFund.getBillingTemplate())
                .investmentManagementName(billingFund.getInvestmentManagementName())
                .investmentManagementAddress(billingFund.getInvestmentManagementAddress())
                .productName(billingFund.getProductName())
                .accountName(billingFund.getAccountName())
                .accountNumber(billingFund.getAccountNumber())
                .customerFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getCustomerFee()))
                .accrualCustodialFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getAccrualCustodialFee()))
                .bis4ValueFrequency(String.valueOf(billingFund.getBis4ValueFrequency()))
                .bis4TransactionFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getBis4TransactionFee()))
                .bis4AmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getBis4AmountDue()))
                .subTotal(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getSubTotal()))
                .vatFee(ConvertBigDecimalUtil.formattedVatFee(billingFund.getVatFee()))
                .vatAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getVatAmountDue()))
                .kseiValueFrequency(String.valueOf(billingFund.getKseiValueFrequency()))
                .kseiTransactionFee(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getKseiTransactionFee()))
                .kseiAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getKseiAmountDue()))
                .totalAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(billingFund.getTotalAmountDue()))
                .build();
    }

    private static List<BillingFundDTO> mapToDTOList(List<BillingFund> billingFundList) {
        return billingFundList.stream()
                .map(FundGeneratePDFServiceImpl::mapToDTO)
                .toList();
    }

}
