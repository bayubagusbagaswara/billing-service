package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.constant.FeeParameterNameConstant;
import com.bayu.billingservice.constant.SkTransactionTypeConstant;
import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.service.FeeParameterService;
import com.bayu.billingservice.service.FundService;
import com.bayu.billingservice.service.SkTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundServiceImpl implements FundService {

    private final SkTransactionService skTransactionService;
    private final FeeParameterService feeParameterService;

    @Override
    public List<BillingFundDTO> calculate(List<FeeReportRequest> request, String month, int year) {

        List<String> nameList = new ArrayList<>();
        nameList.add(FeeParameterNameConstant.BI_SSSS);
        nameList.add(FeeParameterNameConstant.KSEI);
        nameList.add(FeeParameterNameConstant.VAT);

        Map<String, BigDecimal> feeParameterMap = feeParameterService.getValueByNameList(nameList);
        BigDecimal bis4Fee = feeParameterMap.get(FeeParameterNameConstant.BI_SSSS);
        BigDecimal kseiFee = feeParameterMap.get(FeeParameterNameConstant.KSEI);
        BigDecimal vatFee = feeParameterMap.get(FeeParameterNameConstant.VAT);

        List<BillingFundDTO> billingFundDTOList = new ArrayList<>();

        BigDecimal accrualCustodialFee;
        int transactionBISSSSTotal;
        int transactionCBESTTotal;
        BigDecimal amountDueBis4;
        BigDecimal totalAmountDueBeforeTax;
        BigDecimal amountDueVat;
        BigDecimal amountDueKSEI;
        BigDecimal totalAmountDue;

        for (FeeReportRequest feeReportRequest : request) {
            String aid = feeReportRequest.getPortfolioCode();
            BigDecimal customerFee = feeReportRequest.getCustomerFee();

            List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(aid, month, year);

            int[] filteredTransactionsType = filterTransactionsType(skTransactionList);
            transactionCBESTTotal = filteredTransactionsType[0];
            transactionBISSSSTotal = filteredTransactionsType[1];

            accrualCustodialFee = calculateAccrualCustodialFee(customerFee);

            amountDueBis4 = calculateAmountDueBis4(transactionBISSSSTotal, bis4Fee);

            totalAmountDueBeforeTax = calculateTotalAmountDueBeforeVat(accrualCustodialFee, amountDueBis4);

            amountDueVat = calculateAmountDueVat(totalAmountDueBeforeTax, vatFee);

            amountDueKSEI = calculateAmountDueKSEI(transactionCBESTTotal, kseiFee);

            totalAmountDue = calculateTotalAmountDue(totalAmountDueBeforeTax, amountDueVat, amountDueKSEI);

            // Map to Entity Billing Fund List

            BillingFundDTO billingFundDTO = BillingFundDTO.builder()
                    .portfolioCode(aid)
                    .period(month + " " + year)
                    .amountDueAccrualCustody(String.valueOf(accrualCustodialFee))
                    .valueFrequencyBis4(String.valueOf(transactionBISSSSTotal))
                    .bis4Fee(String.valueOf(bis4Fee))
                    .amountDueBis4(String.valueOf(amountDueBis4))
                    .totalNominalBeforeTax(String.valueOf(totalAmountDueBeforeTax))
                    .vatFee(String.valueOf(vatFee))
                    .amountDueVat(String.valueOf(amountDueVat))
                    .valueFrequencyKSEI(String.valueOf(transactionCBESTTotal))
                    .kseiFee(String.valueOf(kseiFee))
                    .amountDueKSEI(String.valueOf(amountDueKSEI))
                    .totalAmountDue(String.valueOf(totalAmountDue))
                    .build();

            billingFundDTOList.add(billingFundDTO);
        }
        return billingFundDTOList;
    }

    private int[] filterTransactionsType(List<SkTransaction> transactionList) {
        int transactionCBESTTotal = 0;
        int transactionBIS4Total = 0;

        for (SkTransaction skTransaction : transactionList) {
            String settlementSystem = skTransaction.getSettlementSystem();
            if (settlementSystem != null) {
                if (SkTransactionTypeConstant.TRANSACTION_CBEST.equalsIgnoreCase(settlementSystem)) {
                    transactionCBESTTotal++;
                } else if (SkTransactionTypeConstant.TRANSACTION_BI_SSSS.equalsIgnoreCase(settlementSystem)) {
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
                .divide(BigDecimal.valueOf(1.11), 4, RoundingMode.HALF_UP)  // Divide by 1.11 with 4 decimal places, rounding up
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateAmountDueBis4(int transactionBISSSSTotal, BigDecimal bis4Fee) {
        return new BigDecimal(transactionBISSSSTotal)
                .multiply(bis4Fee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDueBeforeVat(BigDecimal accrualCustodialFee, BigDecimal amountDueBis4) {
        return accrualCustodialFee.add(amountDueBis4).setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateAmountDueVat(BigDecimal totalAmountDueBeforeTax, BigDecimal vatFee) {
        return totalAmountDueBeforeTax
                .multiply(vatFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateAmountDueKSEI(int transactionCBESTTotal, BigDecimal kseiFee) {
        return new BigDecimal(transactionCBESTTotal)
                .multiply(kseiFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDue(BigDecimal totalAmountDueBeforeTax, BigDecimal amountDueVat, BigDecimal amountDueKSEI) {
        return totalAmountDueBeforeTax
                .add(amountDueVat)
                .add(amountDueKSEI)
                .setScale(0, RoundingMode.HALF_UP);
    }

}
