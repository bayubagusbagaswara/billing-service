package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.billing.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.fund.BillingFundParameter;
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
        List<BillingCalculationErrorMessageDTO> errorMessageList = new ArrayList<>();

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
                Customer customer = customerService.getByCustomerCodeAndSubCodeAndBillingCategoryAndBillingType(aid, "", billingCategory, billingType);
                String miCode = customer.getMiCode();
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(miCode);
                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(aid, "November", 2023);
                log.info("Customer code: {}, sk transaction size: {}", customer.getCustomerCode(), skTransactionList.size());

                if (!skTransactionList.isEmpty()) {
                    Optional<BillingFund> existingBillingFund = billingFundRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(aid, billingCategory, billingType, month, year);
                    if (existingBillingFund.isEmpty() || Boolean.TRUE.equals(!existingBillingFund.get().getPaid())) {
                        existingBillingFund.ifPresent(this::deleteExistingBillingFund);
                        BillingFundParameter billingFundParams = new BillingFundParameter(
                                customer, investmentManagementDTO, dateNow, month, year, skTransactionList,
                                customerFee, bis4TransactionFee, kseiTransactionFee, vatFee
                        );
                        BillingFund billingFund = createBillingFund(billingFundParams);
                        String number = billingNumberService.generateSingleNumber(monthNameNow, yearNow);
                        billingFund.setBillingNumber(number);
                        billingFundRepository.save(billingFund);
                        billingNumberService.saveSingleNumber(number);
                        totalDataSuccess++;
                    } else {
                        addErrorMessage(errorMessageList, customer.getCustomerCode(), "Billing already paid for period " + month + " " + year);
                        totalDataFailed++;
                    }
                } else {
                    addErrorMessage(errorMessageList, customer.getCustomerCode(), "No transaction data from SkTrans for period " + month + " " + year);
                    totalDataFailed++;
                }
            } catch(Exception e) {
                log.error("Error processing customer code {}: {}", aid, e.getMessage(), e);
                handleGeneralError(aid, e, validationErrors, errorMessageList);
                totalDataFailed++;
            }
        }

        log.info("Total successful calculations: {}, total failed calculations: {}", totalDataSuccess, totalDataFailed);
        return new BillingCalculationResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private BillingFund createBillingFund(BillingFundParameter params) {
        int[] filteredTransactionsType = skTransactionService.filterTransactionsType(params.getSkTransactionList());
        int transactionCBESTTotal = filteredTransactionsType[0];
        int transactionBISSSSTotal = filteredTransactionsType[1];

        BigDecimal accrualCustodialFee = calculateAccrualCustodialFee(params.getCustomerFee());
        BigDecimal bis4AmountDue = calculateBis4AmountDue(transactionBISSSSTotal, params.getBis4TransactionFee());
        BigDecimal subTotal = calculateSubTotal(accrualCustodialFee, bis4AmountDue);
        BigDecimal vatAmountDue = calculateVATAmountDue(subTotal, params.getVatFee());
        BigDecimal kseiAmountDue = calculateKSEIAmountDue(transactionCBESTTotal, params.getKseiTransactionFee());
        BigDecimal totalAmountDue = calculateTotalAmountDue(subTotal, vatAmountDue, kseiAmountDue);

        return BillingFund.builder()
                .createdAt(params.getDateNow())
                .updatedAt(params.getDateNow())
                .approvalStatus(ApprovalStatus.PENDING)
                .billingStatus(BillingStatus.GENERATED)
                .customerCode(params.getCustomer().getCustomerCode())
                .customerName(params.getCustomer().getCustomerName())
                .month(params.getMonth())
                .year(params.getYear())
                .billingPeriod(params.getMonth() + " " + params.getYear())
                .billingStatementDate(ConvertDateUtil.convertInstantToString(params.getDateNow()))
                .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(params.getDateNow()))
                .billingCategory(params.getCustomer().getBillingCategory())
                .billingType(params.getCustomer().getBillingType())
                .billingTemplate(params.getCustomer().getBillingTemplate())
                .investmentManagementName(params.getInvestmentManagementDTO().getName())
                .investmentManagementAddress1(params.getInvestmentManagementDTO().getAddress1())
                .investmentManagementAddress2(params.getInvestmentManagementDTO().getAddress2())
                .investmentManagementAddress3(params.getInvestmentManagementDTO().getAddress3())
                .investmentManagementAddress4(params.getInvestmentManagementDTO().getAddress4())
                .investmentManagementEmail(params.getInvestmentManagementDTO().getEmail())
                .investmentManagementUniqueKey(params.getInvestmentManagementDTO().getUniqueKey())
                .gefuCreated(false)
                .paid(false)
                .account(params.getCustomer().getAccount())
                .accountName(params.getCustomer().getAccountName())
                .currency(params.getCustomer().getCurrency())
                .accrualCustodialFee(accrualCustodialFee)
                .bis4TransactionValueFrequency(transactionBISSSSTotal)
                .bis4TransactionFee(params.getBis4TransactionFee())
                .bis4TransactionAmountDue(bis4AmountDue)
                .subTotal(subTotal)
                .vatFee(params.getVatFee())
                .vatAmountDue(vatAmountDue)
                .kseiTransactionValueFrequency(transactionCBESTTotal)
                .kseiTransactionFee(params.getKseiTransactionFee())
                .kseiTransactionAmountDue(kseiAmountDue)
                .totalAmountDue(totalAmountDue)
                .build();
    }

    private void addErrorMessage(List<BillingCalculationErrorMessageDTO> calculationErrorMessages, String customerCode, String message) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(message);
        calculationErrorMessages.add(new BillingCalculationErrorMessageDTO(customerCode, errorMessages));
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
