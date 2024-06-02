package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.BillingCalculationResponse;
import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.*;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.BillingStatus;
import com.bayu.billingservice.model.enumerator.FeeParameter;
import com.bayu.billingservice.repository.BillingCoreRepository;
import com.bayu.billingservice.service.*;
import com.bayu.billingservice.util.ConvertDateUtil;
import com.bayu.billingservice.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreType1ServiceImpl implements CoreType1Service {

    private final BillingCoreRepository billingCoreRepository;
    private final CustomerService customerService;
    private final InvestmentManagementService investmentManagementService;
    private final FeeParameterService feeParameterService;
    private final SkTransactionService skTransactionService;
    private final SfValRgDailyService sfValRgDailyService;
    private final BillingNumberService billingNumberService;
    private final ConvertDateUtil convertDateUtil;

    @Override
    public BillingCalculationResponse calculate(CoreCalculateRequest request) {
        log.info("Start core billing calculation type 1 with a data request: {}", request);

        /* initialize response */
        Integer totalDataSuccess = 0;
        Integer totalDataFailed = 0;
        List<BillingCalculationErrorMessageDTO> errorMessageList = new ArrayList<>();

        /* initialize billing variable */
        Instant dateNow = Instant.now();
        String categoryUpperCase = request.getCategory().toUpperCase();
        String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());

        /* get data data month minus 1 */
        Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
        String monthNameMinus1 = monthMinus1.get("monthName");
        int yearMinus1 = Integer.parseInt(monthMinus1.get("year"));

        /* get data month now */
        Map<String, String> monthNow = convertDateUtil.getMonthNow();
        String monthNameNow = monthNow.get("monthName");
        int yearNow = Integer.parseInt(monthNow.get("year"));

        /* get data fee parameter */
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameter.VAT.getValue());

        /* start calculation */

        /* get all data customer Core Type 1*/
        List<Customer> customerList = customerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

        /* looping customer list */
        for (Customer customer : customerList) {
            /* Get all important data from customers */
            String customerCode = customer.getCustomerCode();
            String customerName = customer.getCustomerName();
            BigDecimal customerMinimumFee = customer.getCustomerMinimumFee();
            BigDecimal customerSafekeepingFee = customer.getCustomerSafekeepingFee();
            BigDecimal transactionHandlingFee = customer.getCustomerTransactionHandling();
            String billingCategory = customer.getBillingCategory();
            String billingType = customer.getBillingType();
            String billingTemplate = customer.getBillingTemplate();
            String miCode = customer.getMiCode();
            String account = customer.getAccount();
            String accountName = customer.getAccountName();
            String currency = customer.getCurrency();
            List<String> validationErrors = new ArrayList<>();

            try {
                /* get data investment management */
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(miCode);

                /* get data sk transaction */
                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(customerCode, monthNameMinus1, yearMinus1);

                /* get data rg daily */
                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(customerCode, monthNameMinus1, yearMinus1);

                Optional<BillingCore> existingBillingCore = billingCoreRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(customerCode, billingCategory, billingType, monthNameMinus1, yearMinus1);
                if (existingBillingCore.isEmpty() || Boolean.TRUE.equals(!existingBillingCore.get().getPaid())) {
                    existingBillingCore.ifPresent(this::deleteExistingBillingCore);
                    /* calculation process */
                    Integer transactionHandlingValueFrequency = calculateTransactionHandlingValueFrequency(customerCode, skTransactionList);
                    BigDecimal transactionHandlingAmountDue = calculateTransactionHandlingAmountDue(customerCode, transactionHandlingFee, transactionHandlingValueFrequency);
                    BigDecimal safekeepingValueFrequency = calculateSafekeepingValueFrequency(customerCode, sfValRgDailyList);
                    BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(customerCode, sfValRgDailyList);
                    BigDecimal subTotal = calculateSubTotalAmountDue(customerCode, transactionHandlingAmountDue, safekeepingAmountDue);
                    BigDecimal vatAmountDue = calculateVatAmountDue(customerCode, subTotal, vatFee);
                    BigDecimal totalAmountDue = calculateTotalAmountDue(customerCode, subTotal, vatAmountDue);

                    /* build billing core */
                    BillingCore billingCore = BillingCore.builder()
                            .createdAt(dateNow)
                            .updatedAt(dateNow)
                            .approvalStatus(ApprovalStatus.PENDING)
                            .billingStatus(BillingStatus.GENERATED)
                            .customerCode(customerCode)
                            .customerName(customerName)
                            .month(monthNameMinus1)
                            .year(yearMinus1)
                            .billingPeriod(monthNameMinus1 + " " + yearMinus1)
                            .billingStatementDate(ConvertDateUtil.convertInstantToString(dateNow))
                            .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(dateNow))
                            .billingCategory(billingCategory)
                            .billingType(billingType)
                            .billingTemplate(billingTemplate)
                            .investmentManagementName(investmentManagementDTO.getName())
                            .investmentManagementAddress1(investmentManagementDTO.getAddress1())
                            .investmentManagementAddress2(investmentManagementDTO.getAddress2())
                            .investmentManagementAddress3(investmentManagementDTO.getAddress3())
                            .investmentManagementAddress4(investmentManagementDTO.getAddress4())
                            .investmentManagementEmail(investmentManagementDTO.getEmail())
                            .investmentManagementUniqueKey(investmentManagementDTO.getUniqueKey())
                            .customerMinimumFee(customerMinimumFee)
                            .customerSafekeepingFee(customerSafekeepingFee)
                            .account(account)
                            .accountName(accountName)
                            .currency(currency)
                            .transactionHandlingValueFrequency(transactionHandlingValueFrequency)
                            .transactionHandlingFee(transactionHandlingFee)
                            .transactionHandlingAmountDue(transactionHandlingAmountDue)
                            .safekeepingValueFrequency(safekeepingValueFrequency)
                            .safekeepingFee(customerSafekeepingFee)
                            .safekeepingAmountDue(safekeepingAmountDue)
                            .subTotal(subTotal)
                            .vatFee(vatFee)
                            .vatAmountDue(vatAmountDue)
                            .totalAmountDue(totalAmountDue)
                            .gefuCreated(false)
                            .paid(false)
                            .build();

                    String number = billingNumberService.generateSingleNumber(monthNameNow, yearNow);
                    billingCore.setBillingNumber(number);
                    billingCoreRepository.save(billingCore);
                    billingNumberService.saveSingleNumber(number);
                    totalDataSuccess++;
                } else {
                    addErrorMessage(errorMessageList, customer.getCustomerCode(), "Billing already paid for period " + monthNameMinus1 + " " + yearMinus1);
                    totalDataFailed++;
                }
            } catch (Exception e) {
                log.error("Error processing customer code {}: {}", customerCode, e.getMessage(), e);
                handleGeneralError(customerCode, e, validationErrors, errorMessageList);
                totalDataFailed++;
            }
        }
        log.info("Total successful calculations: {}, total failed calculations: {}", totalDataSuccess, totalDataFailed);
        return new BillingCalculationResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }


    private static Integer calculateTransactionHandlingValueFrequency(String aid, List<SkTransaction> skTransactionList) {
        Integer totalTransactionHandling = skTransactionList.size();
        log.info("[Core Type 1] Total transaction handling Aid '{}' is '{}'", aid, totalTransactionHandling);
        return totalTransactionHandling;
    }

    private static BigDecimal calculateTransactionHandlingAmountDue(String aid, BigDecimal transactionHandlingFee, int transactionHandlingValueFrequency) {
        BigDecimal transactionHandlingAmountDue = transactionHandlingFee.multiply(new BigDecimal(transactionHandlingValueFrequency).setScale(0, RoundingMode.HALF_UP));
        log.info("[Core Type 1] Transaction handling amount due Aid '{}' is '{}'", aid, transactionHandlingAmountDue);
        return transactionHandlingAmountDue;
    }

    private static BigDecimal calculateSafekeepingValueFrequency(String aid, List<SfValRgDaily> sfValRgDailyList) {
        // Find the latest entries
        List<SfValRgDaily> latestEntries = sfValRgDailyList.stream()
                .filter(entry -> entry.getDate().equals(sfValRgDailyList.stream()
                        .map(SfValRgDaily::getDate)
                        .max(Comparator.naturalOrder())
                        .orElse(null)))
                .toList();
        for (SfValRgDaily latestEntry : latestEntries) {
            log.info("Date '{}', Security Name '{}'", latestEntry.getDate(), latestEntry.getSecurityName());
        }

        // Calculate safekeepingValueFrequency
        BigDecimal safekeepingValueFrequency = latestEntries.stream()
                .map(SfValRgDaily::getMarketValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 1] Safekeeping value frequency Aid '{}' is '{}'", aid, safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private static BigDecimal calculateSafekeepingAmountDue(String aid, List<SfValRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingAmountDue = sfValRgDailyList.stream()
                .map(SfValRgDaily::getEstimationSafekeepingFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 1] Safekeeping amount due Aid '{}' is '{}'", aid, safekeepingAmountDue);
        return safekeepingAmountDue;
    }

    private static BigDecimal calculateSubTotalAmountDue(String aid, BigDecimal transactionHandlingAmountDue, BigDecimal safekeepingAmountDue) {
        BigDecimal subTotalAmountDue = transactionHandlingAmountDue.add(safekeepingAmountDue).setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 1] Sub total amount due Aid '{}' is '{}'", aid, subTotalAmountDue);
        return subTotalAmountDue;
    }

    private static BigDecimal calculateVatAmountDue(String aid, BigDecimal subTotalAmountDue, BigDecimal vatFee) {
        BigDecimal vatAmountDue = subTotalAmountDue.multiply(vatFee).setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 1] VAT amount due Aid '{}' is '{}'", aid, vatAmountDue);
        return vatAmountDue;
    }

    private static BigDecimal calculateTotalAmountDue(String aid, BigDecimal subTotalAmountDue, BigDecimal vatAmountDue) {
        BigDecimal totalAmountDue = subTotalAmountDue.add(vatAmountDue).setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 1] Total amount due Aid '{}' is '{}'", aid, totalAmountDue);
        return totalAmountDue;
    }

    private void handleGeneralError(String string, Exception e, List<String> validationErrors, List<BillingCalculationErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        validationErrors.add(e.getMessage());
        errorMessageList.add(new BillingCalculationErrorMessageDTO(string.isEmpty() ? "unknown" : string, validationErrors));
    }

    private void deleteExistingBillingCore(BillingCore existBillingCore) {
        String billingNumber = existBillingCore.getBillingNumber();
        billingCoreRepository.delete(existBillingCore);
        billingNumberService.deleteByBillingNumber(billingNumber);
    }

    private void addErrorMessage(List<BillingCalculationErrorMessageDTO> calculationErrorMessages, String customerCode, String message) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(message);
        calculationErrorMessages.add(new BillingCalculationErrorMessageDTO(customerCode, errorMessages));
    }

}
