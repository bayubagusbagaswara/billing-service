package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.service.FundService;
import com.bayu.billingservice.service.SkTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FundServiceImpl implements FundService {

    private final SkTransactionService skTransactionService;

    public FundServiceImpl(SkTransactionService skTransactionService) {
        this.skTransactionService = skTransactionService;
    }

    @Override
    public List<BillingFundDTO> calculate(List<FeeReportRequest> request, String month, int year) {
        List<BillingFundDTO> billingFundDTOList = new ArrayList<>();

        for (FeeReportRequest feeReportRequest : request) {
            String aid = feeReportRequest.getPortfolioCode();
            BigDecimal customerFee = feeReportRequest.getCustomerFee();

            List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(aid, month, year);

            BillingFundDTO billingFundDTO = filterTransactionsType(aid, customerFee, month, year, skTransactionList);

            billingFundDTOList.add(billingFundDTO);
        }
        return billingFundDTOList;
    }

    private BillingFundDTO filterTransactionsType(String portfolioCode, BigDecimal customerFee, String currentMonthName, int currentYear, List<SkTransaction> transactionList) {
        int transactionCBESTTotal = 0;
        int transactionBIS4Total = 0;

        for (SkTransaction skTransaction : transactionList) {
            String settlementSystem = skTransaction.getSettlementSystem();
            if (settlementSystem != null) {
                if ("CBEST".equalsIgnoreCase(settlementSystem)) {
                    transactionCBESTTotal++;
                } else if ("BI-SSSS".equalsIgnoreCase(settlementSystem)) {
                    transactionBIS4Total++;
                }
            }
        }
        log.info("Total KSEI : {}", transactionCBESTTotal);
        log.info("Total BI-S4 : {}", transactionBIS4Total);

        return calculateBillingFund(portfolioCode, customerFee,
                currentMonthName, currentYear,
                transactionCBESTTotal, transactionBIS4Total);
    }

    private BillingFundDTO calculateBillingFund(String portfolioCode, BigDecimal customerFee,
                                                String currentMonthName, int currentYear,
                                                int transactionCBESTTotal, int transactionBIS4Total) {

        BigDecimal accrualCustodialFee = customerFee
                        .divide(BigDecimal.valueOf(1.11), 4, RoundingMode.HALF_UP)  // Divide by 1.11 with 4 decimal places, rounding up
                        .setScale(0, RoundingMode.HALF_UP);
        log.info("Accrual Custodial Fee : {}", accrualCustodialFee);

        Integer valueFrequencyS4 = transactionBIS4Total;
        log.info("Value Frequency S4 : {}", valueFrequencyS4);

        BigDecimal s4Fee = new BigDecimal(23000);
        log.info("S4 Fee : {}", s4Fee);

        BigDecimal amountDueS4 = new BigDecimal(valueFrequencyS4).multiply(s4Fee).setScale(0, RoundingMode.HALF_UP);
        log.info("Amount Due S4 : {}", amountDueS4);

        BigDecimal totalNominalBeforeTax = accrualCustodialFee.add(amountDueS4).setScale(0, RoundingMode.HALF_UP);
        log.info("Total Nominal Before Tax : {}", totalNominalBeforeTax);

        double taxFee = 0.11; // 11%
        log.info("Tax Fee : {}", taxFee);

        BigDecimal amountDueTax = totalNominalBeforeTax.multiply(BigDecimal.valueOf(taxFee)).setScale(0, RoundingMode.HALF_UP);
        log.info("Amount Due Tax : {}", amountDueTax);

        Integer valueFrequencyKSEI = transactionCBESTTotal;
        log.info("Value Frequency KSEI : {}", valueFrequencyKSEI);

        BigDecimal kseiFee = new BigDecimal(22200);
        log.info("KSEI Fee : {}", kseiFee);

        BigDecimal amountDueKSEI = new BigDecimal(valueFrequencyKSEI).multiply(kseiFee).setScale(0, RoundingMode.HALF_UP);
        log.info("Amount Due KSEI : {}", amountDueKSEI);

        BigDecimal totalAmountDue = totalNominalBeforeTax
                .add(amountDueTax)
                .add(amountDueKSEI)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("Total Amount Due : {}", totalAmountDue);

        return BillingFundDTO.builder()
                .portfolioCode(portfolioCode)
                .period(currentMonthName + " " + currentYear)
                .amountDueAccrualCustody(accrualCustodialFee)
                .valueFrequencyS4(valueFrequencyS4)
                .s4Fee(s4Fee)
                .amountDueS4(amountDueS4)
                .totalNominalBeforeTax(totalNominalBeforeTax)
                .taxFee(taxFee)
                .amountDueTax(amountDueTax)
                .valueFrequencyKSEI(valueFrequencyKSEI)
                .kseiFee(kseiFee)
                .amountDueKSEI(amountDueKSEI)
                .totalAmountDue(totalAmountDue)
                .build();
    }

}
