package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.exception.CalculateBillingException;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.repository.BillingFundRepository;
import com.bayu.billingservice.service.*;
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
import java.util.Optional;

import static com.bayu.billingservice.model.enumerator.ApprovalStatus.PENDING;
import static com.bayu.billingservice.model.enumerator.BillingCategory.FUND;
import static com.bayu.billingservice.model.enumerator.BillingType.TYPE_1;
import static com.bayu.billingservice.model.enumerator.Currency.IDR;
import static com.bayu.billingservice.model.enumerator.FeeParameter.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundCalculateCalculateServiceImpl implements FundCalculateService {

    private final CustomerService customerService;
    private final SkTransactionService skTransactionService;
    private final FeeParameterService feeParameterService;
    private final BillingNumberService billingNumberService;
    private final BillingFundRepository billingFundRepository;
    private final ConvertDateUtil convertDateUtil;

    @Override
    public String calculate(List<FeeReportRequest> request, String month, Integer year) {
        log.info("Start calculate Billing Fund with month '{}' and year '{}'", month, year);
        try {
            String billingCategory = FUND.getValue();
            String billingType = TYPE_1.getValue();

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

            List<CustomerDTO> customerDTOList = customerService.getByBillingCategoryAndBillingType(billingCategory, billingType);

            for (FeeReportRequest feeReportRequest : request) {
                String aid = feeReportRequest.getPortfolioCode();
                BigDecimal customerFee = feeReportRequest.getCustomerFee();

                for (CustomerDTO customerDTO : customerDTOList) {
                    if (customerDTO.getCustomerCode().equalsIgnoreCase(aid)) {
                        List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(aid, month, year);

                        int[] filteredTransactionsType = skTransactionService.filterTransactionsType(skTransactionList);
                        transactionCBESTTotal = filteredTransactionsType[0];
                        transactionBISSSSTotal = filteredTransactionsType[1];

                        accrualCustodialFee = calculateAccrualCustodialFee(customerFee);

                        bis4AmountDue = calculateAmountDueBis4(transactionBISSSSTotal, bis4TransactionFee);

                        subTotal = calculateTotalAmountDueBeforeVat(accrualCustodialFee, bis4AmountDue);

                        vatAmountDue = calculateAmountDueVat(subTotal, vatFee);

                        kseiAmountDue = calculateAmountDueKSEI(transactionCBESTTotal, kseiTransactionFee);

                        totalAmountDue = calculateTotalAmountDue(subTotal, vatAmountDue, kseiAmountDue);

                        Optional<BillingFund> existingBillingFund = billingFundRepository.findByAidAndBillingCategoryAndBillingTypeAndMonthAndYear(
                                aid, FUND.getValue(), TYPE_1.getValue(), month, year
                        );

                        if (existingBillingFund.isPresent()) {
                            BillingFund existBillingFund = existingBillingFund.get();
                            String billingNumber = existBillingFund.getBillingNumber();
                            billingFundRepository.delete(existBillingFund);
                            billingNumberService.deleteByBillingNumber(billingNumber);
                        }

                        Instant dateNow = Instant.now();
                        BillingFund billingFund = BillingFund.builder()

                                .approvalStatus(PENDING)
                                .customerCode(aid)
                                .month(month)
                                .year(year)
                                .billingPeriod(month + " " + year)
                                .billingStatementDate(convertDateUtil.convertInstantToString(dateNow))
                                .billingPaymentDueDate(convertDateUtil.convertInstantToStringPlus14Days(dateNow))
                                .billingCategory(customerDTO.getBillingCategory())
                                .billingType(customerDTO.getBillingType())
                                .billingTemplate(customerDTO.getBillingTemplate())
                                .investmentManagementName(customerDTO.getInvestmentManagementName())
                                .accountName(customerDTO.getAccountName())
                                .accountNumber(customerDTO.getAccountNumber())
                                .accountBank(customerDTO.getAccountBank())
                                .currency(IDR.getValue())
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
                    } else {
                        log.info("Billing Customer AID {} is not found", aid);
                    }
                }
            }

            List<String> numberList = billingNumberService.generateNumberList(billingFundList.size(), month, year);

            int billingFundListSize = billingFundList.size();
            for (int i = 0; i < billingFundListSize; i++) {
                BillingFund billingFund = billingFundList.get(i);
                String billingNumber = numberList.get(i);
                billingFund.setBillingNumber(billingNumber);
            }

            List<BillingFund> billingFundListSaved = billingFundRepository.saveAll(billingFundList);
            billingNumberService.saveAll(numberList);

            log.info("Finished calculate Billing Fund with month '{}' and year '{}'", month, year);
            return "Successfully calculated Billing Funds with a total : " + billingFundListSaved.size();
        } catch (Exception e) {
            log.error("Error when calculate Billing Funds : " + e.getMessage(), e);
            throw new CalculateBillingException("Error when calculate Billing Funds : " + e.getMessage());
        }
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
