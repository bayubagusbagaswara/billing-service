package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.billing.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.billing.BillingContextDate;
import com.bayu.billingservice.dto.fund.BillingFundParameter;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.dto.fund.FundTemplate;
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
        log.info("Start calculate Billing Fund with request size: {}", feeReportRequests.size());

        /* initialize billing variable */
        Instant dateNow = Instant.now();
        String billingCategory = FUND.getValue();
        String billingType = TYPE_1.getValue();

        /* initialize response */
        Integer totalDataSuccess = 0;
        Integer totalDataFailed = 0;
        List<BillingCalculationErrorMessageDTO> errorMessageList = new ArrayList<>();

        /* generate billing context date */
        BillingContextDate contextDate = convertDateUtil.getBillingContextDate(dateNow);

        /* get all data fee parameter */
        BigDecimal bis4TransactionFee = feeParameterService.getValueByName(BI_SSSS.getValue());
        BigDecimal kseiTransactionFee = feeParameterService.getValueByName(KSEI.getValue());
        BigDecimal vatFee = feeParameterService.getValueByName(VAT.getValue());

        /* start calculation */
        for (FeeReportRequest feeReportRequest : feeReportRequests) {
            String aid = feeReportRequest.getPortfolioCode();
            BigDecimal customerFee = feeReportRequest.getCustomerFee();

            try {
                /* get data customer by aid */
                Customer customer = customerService.getByCustomerCodeAndSubCodeAndBillingCategoryAndBillingType(aid, "", billingCategory, billingType);

                /* get data investment management */
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customer.getMiCode());

                /* get data sk transaction */
                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(aid, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get billing data to check whether the data is in the database or not */
                Optional<BillingFund> existingBillingFund = billingFundRepository.findByCustomerCodeAndSubCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(
                        customer.getCustomerCode(), customer.getSubCode(), billingCategory, billingType, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* check paid status. if it is FALSE, it can be regenerated */
                if (existingBillingFund.isEmpty() || Boolean.TRUE.equals(!existingBillingFund.get().getPaid())) {

                    /* delete billing data if it exists in the database */
                    existingBillingFund.ifPresent(this::deleteExistingBillingFund);

                    /* create billing fund */
                    BillingFund billingFund = createBillingFund(contextDate, customer, investmentManagementDTO);

                    /* create fund parameter */
                    BillingFundParameter billingFundParameter = new BillingFundParameter(
                            customerFee, skTransactionList, customer.getCustomerSafekeepingFee(),
                            bis4TransactionFee, kseiTransactionFee, vatFee);

                    /* calculation billing */
                    FundTemplate fundTemplate1 = calculationFund(billingFundParameter);

                    /* update billing core data to include calculated values */
                    updateBillingFundForFundTemplate(billingFund, fundTemplate1);

                    /* create a billing number then set it to the billing core */
                    String number = billingNumberService.generateSingleNumber(contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());
                    billingFund.setBillingNumber(number);

                    /* save to the database */
                    billingFundRepository.save(billingFund);
                    billingNumberService.saveSingleNumber(number);
                    totalDataSuccess++;
                } else {
                    addErrorMessage(errorMessageList, customer.getCustomerCode(), "Billing already paid for period " + contextDate.getMonthNameMinus1() + " " + contextDate.getYearMinus1());
                    totalDataFailed++;
                }
            } catch (Exception e) {
                log.error("Error processing customer code {}: {}", aid, e.getMessage(), e);
                handleGeneralError(aid, e, errorMessageList);
                totalDataFailed++;
            }
        }
        log.info("Total successful calculations: {}, total failed calculations: {}", totalDataSuccess, totalDataFailed);
        return new BillingCalculationResponse(totalDataSuccess, totalDataFailed, errorMessageList);
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

    private void handleGeneralError(String customerCode, Exception e, List<BillingCalculationErrorMessageDTO> errorMessageList) {
        addErrorMessage(errorMessageList, customerCode, e.getMessage());
    }

    private void addErrorMessage(List<BillingCalculationErrorMessageDTO> calculationErrorMessages, String customerCode, String message) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(message);
        calculationErrorMessages.add(new BillingCalculationErrorMessageDTO(customerCode, errorMessages));
    }

    private void deleteExistingBillingFund(BillingFund existBillingFund) {
        String billingNumber = existBillingFund.getBillingNumber();
        billingFundRepository.delete(existBillingFund);
        billingNumberService.deleteByBillingNumber(billingNumber);
    }

    private BillingFund createBillingFund(BillingContextDate contextDate, Customer customer, InvestmentManagementDTO investmentManagementDTO) {
        return BillingFund.builder()
                .createdAt(contextDate.getDateNow())
                .updatedAt(contextDate.getDateNow())
                .approvalStatus(ApprovalStatus.PENDING)
                .billingStatus(BillingStatus.GENERATED)
                .customerCode(customer.getCustomerCode())
                .subCode(customer.getSubCode())
                .customerName(customer.getCustomerName())
                .month(contextDate.getMonthNameMinus1())
                .year(contextDate.getYearMinus1())
                .billingPeriod(contextDate.getBillingPeriod())
                .billingStatementDate(ConvertDateUtil.convertInstantToString(contextDate.getDateNow()))
                .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(contextDate.getDateNow()))
                .billingCategory(customer.getBillingCategory())
                .billingType(customer.getBillingType())
                .billingTemplate(customer.getBillingTemplate())
                .investmentManagementCode(investmentManagementDTO.getCode())
                .investmentManagementName(investmentManagementDTO.getName())
                .investmentManagementAddress1(investmentManagementDTO.getAddress1())
                .investmentManagementAddress2(investmentManagementDTO.getAddress2())
                .investmentManagementAddress3(investmentManagementDTO.getAddress3())
                .investmentManagementAddress4(investmentManagementDTO.getAddress4())
                .investmentManagementEmail(investmentManagementDTO.getEmail())
                .investmentManagementUniqueKey(investmentManagementDTO.getUniqueKey())
                .account(customer.getAccount())
                .accountName(customer.getAccountName())
                .currency(customer.getCurrency())
                .gefuCreated(false)
                .paid(false)
                .build();
    }

    private FundTemplate calculationFund(BillingFundParameter param) {
        int[] filteredTransactionsType = skTransactionService.filterTransactionsType(param.getSkTransactionList());
        int transactionCBESTTotal = filteredTransactionsType[0];
        int transactionBISSSSTotal = filteredTransactionsType[1];

        BigDecimal accrualCustodialFee = calculateAccrualCustodialFee(param.getCustomerFee());
        BigDecimal bis4AmountDue = calculateBis4AmountDue(transactionBISSSSTotal, param.getBis4TransactionFee());
        BigDecimal subTotal = calculateSubTotal(accrualCustodialFee, bis4AmountDue);
        BigDecimal vatAmountDue = calculateVATAmountDue(subTotal, param.getVatFee());
        BigDecimal kseiAmountDue = calculateKSEIAmountDue(transactionCBESTTotal, param.getKseiTransactionFee());
        BigDecimal totalAmountDue = calculateTotalAmountDue(subTotal, vatAmountDue, kseiAmountDue);

        return FundTemplate.builder()
                .accrualCustodialValueFrequency(param.getCustomerFee())
                .accrualCustodialSafekeepingFee(param.getCustomerSafekeepingFee())
                .accrualCustodialFee(accrualCustodialFee)
                .bis4TransactionValueFrequency(transactionBISSSSTotal)
                .bis4TransactionFee(param.getBis4TransactionFee())
                .bis4TransactionAmountDue(bis4AmountDue)
                .subTotal(subTotal)
                .vatFee(param.getVatFee())
                .vatAmountDue(vatAmountDue)
                .kseiTransactionValueFrequency(transactionCBESTTotal)
                .kseiTransactionFee(param.getKseiTransactionFee())
                .kseiTransactionAmountDue(kseiAmountDue)
                .totalAmountDue(totalAmountDue)
                .build();
    }

    private void updateBillingFundForFundTemplate(BillingFund billingFund, FundTemplate fundTemplate) {
        billingFund.setAccrualCustodialValueFrequency(fundTemplate.getAccrualCustodialValueFrequency());
        billingFund.setAccrualCustodialSafekeepingFee(fundTemplate.getAccrualCustodialSafekeepingFee());
        billingFund.setAccrualCustodialFee(fundTemplate.getAccrualCustodialFee());

        billingFund.setBis4TransactionValueFrequency(fundTemplate.getBis4TransactionValueFrequency());
        billingFund.setBis4TransactionFee(fundTemplate.getBis4TransactionFee());
        billingFund.setBis4TransactionAmountDue(fundTemplate.getBis4TransactionAmountDue());

        billingFund.setSubTotal(fundTemplate.getSubTotal());
        billingFund.setVatFee(fundTemplate.getVatFee());
        billingFund.setVatAmountDue(fundTemplate.getVatAmountDue());

        billingFund.setKseiTransactionValueFrequency(fundTemplate.getKseiTransactionValueFrequency());
        billingFund.setKseiTransactionFee(fundTemplate.getKseiTransactionFee());
        billingFund.setKseiTransactionAmountDue(fundTemplate.getKseiTransactionAmountDue());

        billingFund.setTotalAmountDue(fundTemplate.getTotalAmountDue());
    }

}
