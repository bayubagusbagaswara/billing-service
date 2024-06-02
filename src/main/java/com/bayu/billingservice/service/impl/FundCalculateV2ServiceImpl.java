package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.BillingCalculationResponse;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.BillingStatus;
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

import static com.bayu.billingservice.model.enumerator.BillingCategory.*;
import static com.bayu.billingservice.model.enumerator.BillingType.*;
import static com.bayu.billingservice.model.enumerator.FeeParameter.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundCalculateV2ServiceImpl implements FundCalculateV2Service {

    private final BillingFundRepository billingFundRepository;
    private final CustomerService customerService;
    private final InvestmentManagementService investmentManagementService;
    private final SkTransactionService skTransactionService;
    private final FeeParameterService feeParameterService;
    private final BillingNumberService billingNumberService;
    private final ConvertDateUtil convertDateUtil;

    @Override
    public BillingCalculationResponse calculate(List<FeeReportRequest> feeReportRequests, String monthYear) {
        /* initialize billing variable */
        Instant dateNow = Instant.now();
        String billingCategory = FUND.getValue();
        String billingType = TYPE_1.getValue();

        /* initialize response */
        Integer totalDataSuccess = 0;
        Integer totalDataFailed = 0;
        List<BillingCalculationErrorMessageDTO> calculationErrorMessages = new ArrayList<>();

        /* get data month minus 1 */
        Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
        String month = monthMinus1.get("monthName");
        int year = Integer.parseInt(monthMinus1.get("year"));

        /* get data month now */
        Map<String, String> monthNow = convertDateUtil.getMonthNow();
        String monthNameNow = monthNow.get("monthName");
        int yearNow = Integer.parseInt(monthNow.get("year"));

        /* get all data fee parameter */
        BigDecimal bis4TransactionFee = feeParameterService.getValueByName(BI_SSSS.getValue());
        BigDecimal kseiTransactionFee = feeParameterService.getValueByName(KSEI.getValue());
        BigDecimal vatFee = feeParameterService.getValueByName(VAT.getValue());

        /* start calculation */
        for (FeeReportRequest feeReportRequest : feeReportRequests) {
            String aid = feeReportRequest.getPortfolioCode();
            BigDecimal customerFee = feeReportRequest.getCustomerFee();
            List<String> validationErrors = new ArrayList<>();

            try {
                // Get customer data
                Customer customer = customerService.getByCustomerCodeAndSubCodeAndBillingCategoryAndBillingType(aid, "", billingCategory, billingType);

                // Get investment management data
                String miCode = customer.getMiCode();
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(miCode);

                // Get SK transactions
                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(aid, "November", 2023);
                log.info("Customer code: {}, sk transaction size: {}", customer.getCustomerCode(), skTransactionList.size());

                // Process transactions
                if (!skTransactionList.isEmpty()) {
                    Optional<BillingFund> existingBillingFund = billingFundRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(aid, billingCategory, billingType, month, year);
                    log.info("Existing billing fund: {}", existingBillingFund);

                    if (existingBillingFund.isEmpty()) {
                        int[] filteredTransactionsType = skTransactionService.filterTransactionsType(skTransactionList);
                        int transactionCBESTTotal = filteredTransactionsType[0];
                        int transactionBISSSSTotal = filteredTransactionsType[1];
                        BigDecimal accrualCustodialFee = calculateAccrualCustodialFee(customerFee);
                        BigDecimal bis4AmountDue = calculateBis4AmountDue(transactionBISSSSTotal, bis4TransactionFee);
                        BigDecimal subTotal = calculateSubTotal(accrualCustodialFee, bis4AmountDue);
                        BigDecimal vatAmountDue = calculateVATAmountDue(subTotal, vatFee);
                        BigDecimal kseiAmountDue = calculateKSEIAmountDue(transactionCBESTTotal, kseiTransactionFee);
                        BigDecimal totalAmountDue = calculateTotalAmountDue(subTotal, vatAmountDue, kseiAmountDue);

                        // Build and save BillingFund
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
                                .billingStatementDate(ConvertDateUtil.convertInstantToString(dateNow))
                                .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(dateNow))
                                .billingCategory(customer.getBillingCategory())
                                .billingType(customer.getBillingType())
                                .billingTemplate(customer.getBillingTemplate())
                                .investmentManagementName(investmentManagementDTO.getName())
                                .investmentManagementAddress1(investmentManagementDTO.getAddress1())
                                .investmentManagementAddress2(investmentManagementDTO.getAddress2())
                                .investmentManagementAddress3(investmentManagementDTO.getAddress3())
                                .investmentManagementAddress4(investmentManagementDTO.getAddress4())
                                .investmentManagementEmail(investmentManagementDTO.getEmail())
                                .investmentManagementUniqueKey(investmentManagementDTO.getUniqueKey())
                                .gefuCreated(false)
                                .paid(false)
                                .account(customer.getAccount())
                                .accountName(customer.getAccountName())
                                .currency(customer.getCurrency())
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

                        String number = billingNumberService.generateSingleNumber(monthNameNow, yearNow);
                        billingFund.setBillingNumber(number);
                        billingFundRepository.save(billingFund);
                        billingNumberService.saveSingleNumber(number);
                        totalDataSuccess++;
                        log.info("Billing successfully created for customer: {}", customer.getCustomerCode());
                    } else {
                        BillingFund billingFundExist = existingBillingFund.get();
                        if (Boolean.TRUE.equals(billingFundExist.getPaid())) {
                            List<String> errorMessages = new ArrayList<>();
                            errorMessages.add("Billing with code customer " + customer.getCustomerCode() + " for the period " + month + " " + year + " has already been paid so recalculation cannot be carried out");
                            BillingCalculationErrorMessageDTO calculationErrorMessageDTO = new BillingCalculationErrorMessageDTO(customer.getCustomerCode(), errorMessages);
                            calculationErrorMessages.add(calculationErrorMessageDTO);
                            totalDataFailed++;
                        } else {
                            log.info("Deleting existing billing fund for customer: {}", customer.getCustomerCode());
                            existingBillingFund.ifPresent(this::deleteExistingBillingFund);

                            int[] filteredTransactionsType = skTransactionService.filterTransactionsType(skTransactionList);
                            int transactionCBESTTotal = filteredTransactionsType[0];
                            int transactionBISSSSTotal = filteredTransactionsType[1];
                            BigDecimal accrualCustodialFee = calculateAccrualCustodialFee(customerFee);
                            BigDecimal bis4AmountDue = calculateBis4AmountDue(transactionBISSSSTotal, bis4TransactionFee);
                            BigDecimal subTotal = calculateSubTotal(accrualCustodialFee, bis4AmountDue);
                            BigDecimal vatAmountDue = calculateVATAmountDue(subTotal, vatFee);
                            BigDecimal kseiAmountDue = calculateKSEIAmountDue(transactionCBESTTotal, kseiTransactionFee);
                            BigDecimal totalAmountDue = calculateTotalAmountDue(subTotal, vatAmountDue, kseiAmountDue);

                            // Build and save BillingFund
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
                                    .billingStatementDate(ConvertDateUtil.convertInstantToString(dateNow))
                                    .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(dateNow))
                                    .billingCategory(customer.getBillingCategory())
                                    .billingType(customer.getBillingType())
                                    .billingTemplate(customer.getBillingTemplate())
                                    .investmentManagementName(investmentManagementDTO.getName())
                                    .investmentManagementAddress1(investmentManagementDTO.getAddress1())
                                    .investmentManagementAddress2(investmentManagementDTO.getAddress2())
                                    .investmentManagementAddress3(investmentManagementDTO.getAddress3())
                                    .investmentManagementAddress4(investmentManagementDTO.getAddress4())
                                    .investmentManagementEmail(investmentManagementDTO.getEmail())
                                    .investmentManagementUniqueKey(investmentManagementDTO.getUniqueKey())
                                    .gefuCreated(false)
                                    .paid(false)
                                    .account(customer.getAccount())
                                    .accountName(customer.getAccountName())
                                    .currency(customer.getCurrency())
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

                            String number = billingNumberService.generateSingleNumber(monthNameNow, yearNow);
                            billingFund.setBillingNumber(number);
                            billingFundRepository.save(billingFund);
                            billingNumberService.saveSingleNumber(number);
                            totalDataSuccess++;
                        }
                    }
                } else {
                    List<String> errorMessages = new ArrayList<>();
                    errorMessages.add("Customer code " + customer.getCustomerCode() + " does not have transaction data in the SKTrans file for the period " + month + " " + year);
                    BillingCalculationErrorMessageDTO calculationErrorMessageDTO = new BillingCalculationErrorMessageDTO(customer.getCustomerCode(), errorMessages);
                    calculationErrorMessages.add(calculationErrorMessageDTO);
                    totalDataFailed++;
                }
            } catch(Exception e) {
                log.error("Error processing customer code {}: {}", aid, e.getMessage(), e);
                handleGeneralError(aid, e, validationErrors, calculationErrorMessages);
                totalDataFailed++;
            }
        }
        log.info("Total successful calculations: {}, total failed calculations: {}", totalDataSuccess, totalDataFailed);
        return new BillingCalculationResponse(totalDataSuccess, totalDataFailed, calculationErrorMessages);
    }

    private static BigDecimal calculateAccrualCustodialFee(BigDecimal customerFee) {
        return customerFee
                .divide(BigDecimal.valueOf(1.11), 4, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateBis4AmountDue(int transactionBISSSSTotal, BigDecimal bis4TransactionFee) {
        return new BigDecimal(transactionBISSSSTotal)
                .multiply(bis4TransactionFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateSubTotal(BigDecimal accrualCustodialFee, BigDecimal bis4AmountDue) {
        return accrualCustodialFee.add(bis4AmountDue).setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateVATAmountDue(BigDecimal subTotal, BigDecimal vatFee) {
        return subTotal
                .multiply(vatFee)
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateKSEIAmountDue(int transactionCBESTTotal, BigDecimal kseiTransactionFee) {
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

    private void handleGeneralError(String string, Exception e, List<String> validationErrors, List<BillingCalculationErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        validationErrors.add(e.getMessage());
        errorMessageList.add(new BillingCalculationErrorMessageDTO(string.isEmpty() ? "unknown" : string, validationErrors));
    }

    private void deleteExistingBillingFund(BillingFund existBillingFund) {
        String billingNumber = existBillingFund.getBillingNumber();
        billingFundRepository.delete(existBillingFund);
        billingNumberService.deleteByBillingNumber(billingNumber);
    }

}
