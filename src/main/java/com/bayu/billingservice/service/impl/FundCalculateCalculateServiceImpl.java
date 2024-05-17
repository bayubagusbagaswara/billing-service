package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.exception.CalculateBillingException;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.enumerator.*;
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

import static com.bayu.billingservice.model.enumerator.FeeParameter.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundCalculateCalculateServiceImpl implements FundCalculateService {

    private final SkTransactionService skTransactionService;
    private final FeeParameterService feeParameterService;
    private final BillingNumberService billingNumberService;
    private final InvestmentManagementService investmentManagementService;
    private final BillingFundRepository billingFundRepository;
    private final ConvertDateUtil convertDateUtil;
    private final CustomerService customerService;

    @Override
    public String calculate(List<FeeReportRequest> request, String month, Integer year) {
        log.info("Start calculate Billing Fund with month '{}' and year '{}'", month, year);
        try {
            String billingCategory = BillingCategory.FUND.getValue();
            String billingType = BillingType.TYPE_1.getValue();

            List<String> nameList = new ArrayList<>();
            nameList.add(BI_SSSS.getValue());
            nameList.add(KSEI.getValue());
            nameList.add(VAT.getValue());

            Map<String, BigDecimal> feeParameterMap = feeParameterService.getValueByNameList(nameList);
            BigDecimal bis4TransactionFee = feeParameterMap.get(BI_SSSS.getValue());
            BigDecimal kseiTransactionFee = feeParameterMap.get(KSEI.getValue());
            BigDecimal vatFee = feeParameterMap.get(VAT.getValue());

            List<BillingFund> billingFundList = new ArrayList<>();
            Instant dateNow = Instant.now();

            BigDecimal accrualCustodialFee;
            int transactionBISSSSTotal;
            int transactionCBESTTotal;

            BigDecimal bis4AmountDue;
            BigDecimal subTotal;
            BigDecimal vatAmountDue;
            BigDecimal kseiAmountDue;
            BigDecimal totalAmountDue;

            List<Customer> customers = customerService.getAllByBillingCategoryAndBillingType(billingCategory, billingType);

            for (FeeReportRequest feeReportRequest : request) {
                String aid = feeReportRequest.getPortfolioCode();
                BigDecimal customerFee = feeReportRequest.getCustomerFee();

                for (Customer customer : customers) {
                    if (customer.getCustomerCode().equalsIgnoreCase(aid)) {
                        String investmentManagementCode = customer.getInvestmentManagementCode();

                        InvestmentManagementDTO billingMIDTO = investmentManagementService.getByCode(investmentManagementCode);

                        List<SkTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, month, year);

                        if (!skTransactionList.isEmpty()) {
                            int[] filteredTransactionsType = skTranService.filterTransactionsType(skTransactionList);
                            transactionCBESTTotal = filteredTransactionsType[0];
                            transactionBISSSSTotal = filteredTransactionsType[1];

                            accrualCustodialFee = calculateAccrualCustodialFee(customerFee);

                            bis4AmountDue = calculateBis4AmountDue(transactionBISSSSTotal, bis4TransactionFee);

                            subTotal = calculateSubTotal(accrualCustodialFee, bis4AmountDue);

                            vatAmountDue = calculateVATAmountDue(subTotal, vatFee);

                            kseiAmountDue = calculateKSEIAmountDue(transactionCBESTTotal, kseiTransactionFee);

                            totalAmountDue = calculateTotalAmountDue(subTotal, vatAmountDue, kseiAmountDue);

                            Optional<BillingFund> existingBillingFund = billingFundRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(
                                    aid, FUND.getValue(), TYPE_1.getValue(), month, year
                            );

                            if (existingBillingFund.isPresent()) {
                                BillingFund existBillingFund = existingBillingFund.get();
                                String billingNumber = existBillingFund.getBillingNumber();
                                billingFundRepository.delete(existBillingFund);
                                billingNumberService.deleteByBillingNumber(billingNumber);
                            }

                            BillingFund billingFund = BillingFund.builder()
                                    .createdAt(dateNow)
                                    .updatedAt(dateNow)
                                    .approvalStatus(ApprovalStatus.PENDING)
                                    .billingStatus(BillingStatus.GENERATED)
                                    .customerCode(customer.getCustomerCode())
                                    .customerName(customer.getCustomerName())
                                    .month(month)
                                    .year(year)
                                    .billingPeriod(month + " " + year)
                                    .billingStatementDate(convertDateUtil.convertInstantToString(dateNow))
                                    .billingPaymentDueDate(convertDateUtil.convertInstantToStringPlus14Days(dateNow))
                                    .billingCategory(customer.getBillingCategory())
                                    .billingType(customer.getBillingType())
                                    .billingTemplate(customer.getBillingTemplate())
                                    .investmentManagementName(billingMIDTO.getName())
                                    .investmentManagementAddress1(billingMIDTO.getAddress1())
                                    .investmentManagementAddress2(billingMIDTO.getAddress2())
                                    .investmentManagementAddress3(billingMIDTO.getAddress3())
                                    .investmentManagementAddress4(billingMIDTO.getAddress4())
                                    .investmentManagementEmail(billingMIDTO.getEmail())
                                    .accountName(billingCustomer.getAccountName())
                                    .accountNumber(billingCustomer.getAccount())
                                    //.accountBank(billingCustomer.getAccountBank())
                                    .currency(billingCustomer.getCurrency())
                                    .customerFee(customerFee)
                                    .accrualCustodialFee(accrualCustodialFee)
                                    .bis4TransactionValueFrequency(transactionBISSSSTotal)
                                    .bis4TransactionFee(bis4TransactionFee)
                                    .bis4TransactionAmountDue(bis4AmountDue)
                                    .subTotal(subTotal)
                                    .vatFee(vatFee)
                                    .vatAmountDue(vatAmountDue)
                                    .kseiTransactionValueFrequency(transactionCBESTTotal)
                                    .kseiTransactionFee(kseiTransactionFee)
                                    .kseiTransactionAmountDue(kseiAmountDue)
                                    .totalAmountDue(totalAmountDue)
                                    .build();
                            billingFundList.add(billingFund);
                        }
                    } else {
                        log.info("AID '{}' is not match with Customer Code", aid);
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
            log.error("Error when calculate Billing Funds: {}", e.getMessage(), e);
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
