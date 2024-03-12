package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.exception.CalculateBillingException;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.model.enumerator.*;
import com.bayu.billingservice.repository.BillingFundRepository;
import com.bayu.billingservice.service.BillingNumberService;
import com.bayu.billingservice.service.FeeParameterService;
import com.bayu.billingservice.service.FundCalculateService;
import com.bayu.billingservice.service.SkTransactionService;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bayu.billingservice.model.enumerator.FeeParameter.*;
import static com.bayu.billingservice.model.enumerator.SkTransactionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundCalculateCalculateServiceImpl implements FundCalculateService {

    private final SkTransactionService skTransactionService;
    private final FeeParameterService feeParameterService;
    private final BillingNumberService billingNumberService;
    private final BillingFundRepository billingFundRepository;

    @Override
    public String calculate(List<FeeReportRequest> request, String month, Integer year) {
        log.info("Start calculate Billing Fund with month '{}' and year '{}'", month, year);
        try {
            List<String> nameList = new ArrayList<>();
            nameList.add(BI_SSSS.getValue());
            nameList.add(KSEI.getValue());
            nameList.add(VAT.getValue());

            Map<String, BigDecimal> feeParameterMap = feeParameterService.getValueByNameList(nameList);
            BigDecimal bis4TransactionFee = feeParameterMap.get(BI_SSSS.getValue());
            BigDecimal kseiTransactionFee = feeParameterMap.get(KSEI.getValue());
            BigDecimal vatFee = feeParameterMap.get(VAT.getValue());

            List<BillingFund> billingFundList = new ArrayList<>();

            BigDecimal accrualCustodialFee;
            int transactionBISSSSTotal;
            int transactionCBESTTotal;

            BigDecimal bis4AmountDue;
            BigDecimal subTotal;
            BigDecimal vatAmountDue;
            BigDecimal kseiAmountDue;
            BigDecimal totalAmountDue;

            for (FeeReportRequest feeReportRequest : request) {
                String aid = feeReportRequest.getPortfolioCode();
                BigDecimal customerFee = feeReportRequest.getCustomerFee();

                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(aid, month, year);

                int[] filteredTransactionsType = filterTransactionsType(skTransactionList);
                transactionCBESTTotal = filteredTransactionsType[0];
                transactionBISSSSTotal = filteredTransactionsType[1];

                accrualCustodialFee = calculateAccrualCustodialFee(customerFee);

                bis4AmountDue = calculateAmountDueBis4(transactionBISSSSTotal, bis4TransactionFee);

                subTotal = calculateTotalAmountDueBeforeVat(accrualCustodialFee, bis4AmountDue);

                vatAmountDue = calculateAmountDueVat(subTotal, vatFee);

                kseiAmountDue = calculateAmountDueKSEI(transactionCBESTTotal, kseiTransactionFee);

                totalAmountDue = calculateTotalAmountDue(subTotal, vatAmountDue, kseiAmountDue);

                Instant dateNow = Instant.now();
                BillingFund billingFund = BillingFund.builder()
                        .createdAt(dateNow)
                        .updatedAt(dateNow)
                        .approvalStatus(ApprovalStatus.PENDING.getStatus())
                        .aid(aid)
                        .month(month)
                        .year(year)
                        .billingPeriod(month + " " + year)
                        .billingStatementDate(ConvertDateUtil.convertInstantToString(dateNow))
                        .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(dateNow))
                        .billingCategory(BillingCategory.FUND.getValue())
                        .billingType(BillingType.TYPE_1.getValue())
                        .billingTemplate(BillingTemplate.FUND_TEMPLATE.getValue())
                        .investmentManagementName("")
                        .investmentManagementAddress("")
                        .productName("")
                        .accountName("")
                        .accountNumber("")
                        .accountBank("")
                        .currency(Currency.IDR.getValue())
                        .customerFee(customerFee)
                        .accrualCustodialFee(accrualCustodialFee)
                        .bis4ValueFrequency(transactionBISSSSTotal)
                        .bis4TransactionFee(bis4TransactionFee)
                        .bis4AmountDue(bis4AmountDue)
                        .subTotal(subTotal)
                        .vatFee(vatFee)
                        .vatAmountDue(vatAmountDue)
                        .kseiValueFrequency(transactionCBESTTotal)
                        .kseiTransactionFee(kseiTransactionFee)
                        .kseiAmountDue(kseiAmountDue)
                        .totalAmountDue(totalAmountDue)
                        .build();

                billingFundList.add(billingFund);
            }

            List<String> numberList = billingNumberService.generateNumberList(billingFundList.size(), month, year);

            int billingFundListSize = billingFundList.size();
            for (int i = 0; i < billingFundListSize; i++) {
                BillingFund billingFund = billingFundList.get(i);
                String billingNumber = numberList.get(i);
                billingFund.setBillingNumber(billingNumber);
            }

            billingNumberService.saveAll(numberList);
            billingFundRepository.saveAll(billingFundList);

            log.info("Finished calculate Billing Fund with month '{}' and year '{}'", month, year);
            return "Successfully calculated Billing Funds with a total : " + billingFundListSize;
        } catch (Exception e) {
            log.error("Error when calculate Billing Funds : " + e.getMessage(), e);
            throw new CalculateBillingException("Error when calculate Billing Funds : " + e.getMessage());
        }
    }

    private int[] filterTransactionsType(List<SkTransaction> transactionList) {
        int transactionCBESTTotal = 0;
        int transactionBIS4Total = 0;

        for (SkTransaction skTransaction : transactionList) {
            String settlementSystem = skTransaction.getSettlementSystem();
            if (settlementSystem != null) {
                if (TRANSACTION_CBEST.getValue().equalsIgnoreCase(settlementSystem)) {
                    transactionCBESTTotal++;
                } else if (TRANSACTION_BI_SSSS.getValue().equalsIgnoreCase(settlementSystem)) {
                    transactionBIS4Total++;
                }
            }
        }
        log.info("Total KSEI : {}", transactionCBESTTotal);
        log.info("Total BI-S4 : {}", transactionBIS4Total);

        return new int[] {transactionCBESTTotal, transactionBIS4Total};
    }

    private static BigDecimal calculateAccrualCustodialFee(BigDecimal customerFee) {
        return customerFee
                .divide(BigDecimal.valueOf(1.11), 4, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateAmountDueBis4(int transactionBISSSSTotal, BigDecimal bis4TransactionFee) {
        return new BigDecimal(transactionBISSSSTotal)
                .multiply(bis4TransactionFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDueBeforeVat(BigDecimal accrualCustodialFee, BigDecimal bis4AmountDue) {
        return accrualCustodialFee.add(bis4AmountDue).setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateAmountDueVat(BigDecimal subTotal, BigDecimal vatFee) {
        return subTotal
                .multiply(vatFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateAmountDueKSEI(int transactionCBESTTotal, BigDecimal kseiTransactionFee) {
        return new BigDecimal(transactionCBESTTotal)
                .multiply(kseiTransactionFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDue(BigDecimal subTotal, BigDecimal vatAmountDue, BigDecimal kseiAmountDue) {
        return subTotal
                .add(vatAmountDue)
                .add(kseiAmountDue)
                .setScale(0, RoundingMode.HALF_UP);
    }

}
