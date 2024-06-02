package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.billing.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.billing.BillingContextDate;
import com.bayu.billingservice.dto.core.CoreCalculateRequest;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.BillingStatus;
import com.bayu.billingservice.model.enumerator.BillingTemplate;
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
public class CoreType4ServiceImpl implements CoreType4Service {

    private final BillingCoreRepository billingCoreRepository;
    private final CustomerService customerService;
    private final InvestmentManagementService investmentManagementService;
    private final FeeParameterService feeParameterService;
    private final SkTransactionService skTransactionService;
    private final SfValRgDailyService sfValRgDailyService;
    private final KseiSafekeepingFeeService kseiSafekeepingFeeService;
    private final BillingNumberService billingNumberService;
    private final ConvertDateUtil convertDateUtil;

    @Override
    public BillingCalculationResponse calculate(CoreCalculateRequest request) {
        log.info("Start core billing calculation type 4 with a data request: {}", request);

        /* initialize response data */
        Integer totalDataSuccess = 0;
        Integer totalDataFailed = 0;
        List<BillingCalculationErrorMessageDTO> errorMessageList = new ArrayList<>();

        /* initialize data request */
        Instant dateNow = Instant.now();
        String categoryUpperCase = request.getCategory().toUpperCase();
        String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());

        /* generate billing context date */
        BillingContextDate contextDate = getBillingContextDate(dateNow);

        /* get data fee parameters */
        BigDecimal kseiTransactionFee = feeParameterService.getValueByName(FeeParameter.KSEI.getValue());
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameter.VAT.getValue());

        /* get all customer Core Type 3 */
        List<Customer> customerList = customerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

        for (Customer customer : customerList) {
            try {
                String customerCode = customer.getCustomerCode();
                String customerName = customer.getCustomerName();
                String kseiSafeCode = customer.getKseiSafeCode();
                BigDecimal customerMinimumFee = customer.getCustomerMinimumFee();
                BigDecimal customerSafekeepingFee = customer.getCustomerSafekeepingFee();
                BigDecimal transactionHandlingFee = customer.getCustomerTransactionHandling();
                String billingCategory = customer.getBillingCategory();
                String billingType = customer.getBillingType();
                String billingTemplate = customer.getBillingTemplate();
                String investmentManagementCode = customer.getMiCode();
                String account = customer.getAccount();
                String accountName = customer.getAccountName();
                String currency = customer.getCurrency();

                /* get data investment management */
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(investmentManagementCode);

                /* get data sk transaction */
                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get data RG Daily */
                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get data KSEI safekeeping fee */
                BigDecimal kseiSafeFeeAmount = kseiSafekeepingFeeService.calculateAmountFeeByKseiSafeCodeAndMonthAndYear(kseiSafeCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* check and delete existing billing data with the same month and year */
                Optional<BillingCore> existingBillingCore = billingCoreRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(customerCode, billingCategory, billingType, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());
                if (existingBillingCore.isEmpty() || Boolean.TRUE.equals(!existingBillingCore.get().getPaid())) {
                    existingBillingCore.ifPresent(this::deleteExistingBillingCore);

                    BillingCore billingCore = new BillingCore();
                    if (BillingTemplate.CORE_TEMPLATE_5.getValue().equalsIgnoreCase(billingTemplate)) {
                        billingCore = calculateEB(customerCode, customerName, kseiSafeFeeAmount, kseiTransactionFee, skTransactionList);
                    } else if (BillingTemplate.CORE_TEMPLATE_3.getValue().equalsIgnoreCase(billingTemplate)) {
                        billingCore = calculateITAMA(customerCode, customerName, customerSafekeepingFee, vatFee, sfValRgDailyList, transactionHandlingFee);
                    } else {
                        log.info("Customer code '{}' and Template '{}' is not valid for Billing Type 4", customerCode, billingTemplate);
                    }

                    billingCore.setCreatedAt(dateNow);
                    billingCore.setUpdatedAt(dateNow);
                    billingCore.setApprovalStatus(ApprovalStatus.PENDING);
                    billingCore.setBillingStatus(BillingStatus.GENERATED);
                    billingCore.setMonth(contextDate.getMonthNameMinus1());
                    billingCore.setYear(contextDate.getYearMinus1());
                    billingCore.setBillingPeriod(contextDate.getMonthNameMinus1() + " " + contextDate.getYearMinus1());
                    billingCore.setBillingStatementDate(ConvertDateUtil.convertInstantToString(dateNow));
                    billingCore.setBillingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(dateNow));
                    billingCore.setBillingCategory(billingCategory);
                    billingCore.setBillingType(billingType);
                    billingCore.setBillingTemplate(billingTemplate);
                    billingCore.setInvestmentManagementName(investmentManagementDTO.getName());
                    billingCore.setInvestmentManagementAddress1(investmentManagementDTO.getAddress1());
                    billingCore.setInvestmentManagementAddress2(investmentManagementDTO.getAddress2());
                    billingCore.setInvestmentManagementAddress3(investmentManagementDTO.getAddress3());
                    billingCore.setInvestmentManagementAddress4(investmentManagementDTO.getAddress4());
                    billingCore.setInvestmentManagementEmail(investmentManagementDTO.getEmail());
                    billingCore.setInvestmentManagementUniqueKey(investmentManagementDTO.getUniqueKey());
                    billingCore.setCustomerMinimumFee(customerMinimumFee);
                    billingCore.setAccount(account);
                    billingCore.setAccountName(accountName);
                    billingCore.setCurrency(currency);
                    billingCore.setGefuCreated(false);
                    billingCore.setPaid(false);

                    String number = billingNumberService.generateSingleNumber(contextDate.getMonthNameNow(), contextDate.getYearNow());
                    billingCore.setBillingNumber(number);
                    billingCoreRepository.save(billingCore);
                    billingNumberService.saveSingleNumber(number);
                    totalDataSuccess++;
                } else {
                    addErrorMessage(errorMessageList, customer.getCustomerCode(), "Billing already paid for period " + contextDate.getMonthNameMinus1() + " " + contextDate.getYearMinus1());
                    totalDataFailed++;
                }

            } catch (Exception e) {
                handleGeneralError(customer.getCustomerCode(), e, errorMessageList);
                totalDataFailed++;
            }
        }

        log.info("Total successful calculations: {}, total failed calculations: {}", totalDataSuccess, totalDataFailed);
        return new BillingCalculationResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private static BigDecimal calculateSafekeepingValueFrequency(String aid, List<SfValRgDaily> sfValRgDailyList) {
        List<SfValRgDaily> latestEntries = sfValRgDailyList.stream()
                .filter(entry -> entry.getDate().equals(sfValRgDailyList.stream()
                        .map(SfValRgDaily::getDate)
                        .max(Comparator.naturalOrder())
                        .orElse(null)))
                .toList();

        BigDecimal safekeepingValueFrequency = latestEntries.stream()
                .map(SfValRgDaily::getMarketValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 4 ITAMA] Safekeeping value frequency Aid '{}' is '{}'", aid, safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private static BigDecimal calculateSafekeepingAmountDue(String aid, List<SfValRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingAmountDue = sfValRgDailyList.stream()
                .map(SfValRgDaily::getEstimationSafekeepingFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 4 ITAMA] Safekeeping amount due Aid '{}' is '{}'", aid, safekeepingAmountDue);
        return safekeepingAmountDue;
    }

    private static BigDecimal calculateVatAmountDue(String aid, BigDecimal subTotalAmountDue, BigDecimal vatFee) {
        BigDecimal vatAmountDue = subTotalAmountDue.multiply(vatFee)
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 4 ITAMA] VAT amount due Aid '{}' is '{}'", aid, vatAmountDue);
        return vatAmountDue;
    }

    private static BigDecimal calculateTotalAmountDueITAMA(String aid, BigDecimal safekeepingAmountDue, BigDecimal vatAmountDue) {
        BigDecimal totalAmountDueITAMA = safekeepingAmountDue.add(vatAmountDue);
        log.info("[Core Type 4] Total amount due ITAMA Aid '{}' is '{}'", aid, vatAmountDue);
        return totalAmountDueITAMA;
    }

    private static BillingCore calculateITAMA(String aid, String customerName, BigDecimal customerSafekeepingFee, BigDecimal vatFee, List<SfValRgDaily> sfValRgDailyList, BigDecimal transactionHandlingFee) {
        BigDecimal safekeepingValueFrequency = calculateSafekeepingValueFrequency(aid, sfValRgDailyList);
        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(aid, sfValRgDailyList);
        BigDecimal vatAmountDue = calculateVatAmountDue(aid, safekeepingAmountDue, vatFee);
        BigDecimal totalAmountDueITAMA = calculateTotalAmountDueITAMA(aid, safekeepingAmountDue, vatAmountDue);

        Integer transactionHandlingFrequency = 0;
        BigDecimal transactionHandlingAmountDue = BigDecimal.ZERO;
        BigDecimal subTotal = transactionHandlingAmountDue.add(safekeepingAmountDue);

        return BillingCore.builder()
                .customerCode(aid)
                .customerName(customerName)
                .transactionHandlingValueFrequency(transactionHandlingFrequency)
                .transactionHandlingFee(transactionHandlingFee)
                .transactionHandlingAmountDue(transactionHandlingAmountDue)
                .safekeepingValueFrequency(safekeepingValueFrequency)
                .safekeepingFee(customerSafekeepingFee)
                .safekeepingAmountDue(safekeepingAmountDue)
                .subTotal(subTotal)
                .vatFee(vatFee)
                .vatAmountDue(vatAmountDue)
                .totalAmountDue(totalAmountDueITAMA)
                .build();
    }

    private static int calculateTransactionHandlingValueFrequency(String aid, List<SkTransaction> skTransactionList) {
        int totalTransactionHandling = skTransactionList.size();
        log.info("[Core Type 4 EB] Total transaction handling Aid '{}' is '{}'", aid, totalTransactionHandling);
        return totalTransactionHandling;
    }

    private static BigDecimal calculateKSEITransactionAmountDue(String aid, BigDecimal kseiTransactionFee, Integer transactionValueFrequency) {
        BigDecimal kseiTransactionAmountDue = kseiTransactionFee.multiply(new BigDecimal(transactionValueFrequency))
                .setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 4 EB] KSEI transaction amount due Aid '{}' is '{}'", aid, kseiTransactionAmountDue);
        return kseiTransactionAmountDue;
    }

    private static BigDecimal calculateTotalAmountDueEB(String aid, BigDecimal kseiSafekeepingAmountDue, BigDecimal kseiTransactionAmountDue) {
        BigDecimal totalAmountDueEB = kseiSafekeepingAmountDue.add(kseiTransactionAmountDue);
        log.info("[Core Type 4 EB] Total amount due Aid '{}' is '{}'", aid, totalAmountDueEB);
        return totalAmountDueEB;
    }

    private static BillingCore calculateEB(String aid, String customerName, BigDecimal kseiSafeFeeAmount, BigDecimal kseiTransactionFee, List<SkTransaction> skTransactionList) {
        int kseiTransactionValueFrequency = calculateTransactionHandlingValueFrequency(aid, skTransactionList);
        BigDecimal kseiTransactionAmountDue = calculateKSEITransactionAmountDue(aid, kseiTransactionFee, kseiTransactionValueFrequency);
        BigDecimal totalAmountDueEB = calculateTotalAmountDueEB(aid, kseiSafeFeeAmount, kseiTransactionAmountDue);

        return BillingCore.builder()
                .customerCode(aid)
                .customerName(customerName)
                .kseiSafekeepingAmountDue(kseiSafeFeeAmount)
                .kseiTransactionValueFrequency(kseiTransactionValueFrequency)
                .kseiTransactionFee(kseiTransactionFee)
                .kseiTransactionAmountDue(kseiTransactionAmountDue)
                .totalAmountDue(totalAmountDueEB)
                .build();
    }

    private BillingContextDate getBillingContextDate(Instant dateNow) {
        Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
        String monthNameMinus1 = monthMinus1.get("monthName");
        int yearMinus1 = Integer.parseInt(monthMinus1.get("year"));

        Map<String, String> monthNow = convertDateUtil.getMonthNow();
        String monthNameNow = monthNow.get("monthName");
        int yearNow = Integer.parseInt(monthNow.get("year"));

        return new BillingContextDate(dateNow, monthNameMinus1, yearMinus1, monthNameNow, yearNow);
    }

    private void deleteExistingBillingCore(BillingCore existBillingCore) {
        String billingNumber = existBillingCore.getBillingNumber();
        billingCoreRepository.delete(existBillingCore);
        billingNumberService.deleteByBillingNumber(billingNumber);
    }



    private void handleGeneralError(String customerCode, Exception e, List<BillingCalculationErrorMessageDTO> errorMessageList) {
        addErrorMessage(errorMessageList, customerCode, e.getMessage());
    }

    private void addErrorMessage(List<BillingCalculationErrorMessageDTO> errorMessageList, String customerCode, String message) {
        List<String> stringList = new ArrayList<>();
        stringList.add(message);
        errorMessageList.add(new BillingCalculationErrorMessageDTO(customerCode, stringList));
    }
}
