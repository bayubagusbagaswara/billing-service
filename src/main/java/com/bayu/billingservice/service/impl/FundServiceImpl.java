package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.BillingFundDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.service.FundService;
import com.bayu.billingservice.service.SkTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundServiceImpl implements FundService {

    private final SkTransactionService skTransactionService;

    @Override
    public List<BillingFundDTO> generateBillingFund(List<FeeReportRequest> request, String date) {
        List<BillingFundDTO> billingFundDTOList = new ArrayList<>();

        // kita harus mendapatkan dulu portfolio code dari request list
        for (FeeReportRequest feeReportRequest : request) {
            String portfolioCode = feeReportRequest.getPortfolioCode();
            BigDecimal customerFee = feeReportRequest.getCustomerFee();

            List<SkTransaction> skTransactionList = skTransactionService.getAllByPortfolioCode(portfolioCode);

            // Date bisa dibawa dari depan
            // format date dari depan : Nov 2023
            // Parse the month name using a custom formatter and TemporalQuery
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("MMM yyyy")
                    .toFormatter(Locale.ENGLISH);

            TemporalAccessor temporalAccessor = formatter.parse(date);
            LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));

            // Set the day of the month to 1 to represent a fixed day
            LocalDate fixedDate = parsedDate.withDayOfMonth(30);

            int currentMonth = parsedDate.getMonthValue();
            Month currentMonthName = parsedDate.getMonth();
            String monthName = currentMonthName.getDisplayName(TextStyle.SHORT, java.util.Locale.getDefault());

            int currentYear = parsedDate.getYear();

            // Filter transactions for the current month
            List<SkTransaction> filteredTransactions = skTransactionList.stream()
                    .filter(skTransaction -> skTransaction.getSettlementDate().getYear() == currentYear && skTransaction.getSettlementDate().getMonthValue() == currentMonth)
                    .toList();

            BillingFundDTO billingFundDTO = filterTransactionsType(portfolioCode, customerFee,
                    monthName, currentYear,
                    filteredTransactions);

            billingFundDTOList.add(billingFundDTO);
        }
        return billingFundDTOList;
    }

    static class MonthYearQuery implements TemporalQuery<LocalDate> {
        @Override
        public LocalDate queryFrom(TemporalAccessor temporalAccessor) {
            int year = temporalAccessor.get(ChronoField.YEAR);
            int month = temporalAccessor.get(ChronoField.MONTH_OF_YEAR);
            return LocalDate.of(year, month, 1); // Day set to 1 for the first day of the month
        }
    }

    private BillingFundDTO filterTransactionsType(String portfolioCode, BigDecimal customerFee,
                                        String currentMonthName, int currentYear,
                                        List<SkTransaction> transactionList) {
        // pisah antara transaction
        int transactionCBESTTotal = 0;
        int transactionBIS4Total = 0;

        for (SkTransaction skTransaction : transactionList) {
            if (null != skTransaction.getSettlementSystem()) {
                if ("CBEST".equalsIgnoreCase(skTransaction.getSettlementSystem())) {
                    transactionCBESTTotal += 1;
                } else if ("BI-SSSS".equalsIgnoreCase(skTransaction.getSettlementSystem())) {
                    transactionBIS4Total += 1;
                }
            }
        }
        log.info("Total KSEI : {}", transactionCBESTTotal);
        log.info("Total BI-S4 : {}", transactionBIS4Total);

        // panggil method calculate billing fund
        return calculateBillingFund(portfolioCode, customerFee,
                currentMonthName, currentYear,
                transactionCBESTTotal, transactionBIS4Total);
    }

    private BillingFundDTO calculateBillingFund(String portfolioCode, BigDecimal customerFee,
                                                String currentMonthName, int currentYear,
                                                int transactionCBESTTotal, int transactionBIS4Total) {

        BigDecimal accrualCustodialFee = customerFee
                        .divide(BigDecimal.valueOf(1.11), 4, RoundingMode.CEILING)  // Divide by 1.11 with 4 decimal places, rounding up
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

        BigDecimal amountDueTax = totalNominalBeforeTax.multiply(new BigDecimal(taxFee)).setScale(0, RoundingMode.HALF_UP);
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
