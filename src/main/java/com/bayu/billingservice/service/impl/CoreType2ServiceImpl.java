package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.BillingCalculationResponse;
import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.dto.core.BillingContextDate;
import com.bayu.billingservice.dto.core.CoreTemplate3;
import com.bayu.billingservice.dto.core.CoreType2Parameter;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SkTransaction;
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
public class CoreType2ServiceImpl implements CoreType2Service {

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
        log.info("Start core billing calculation type 2 with a data request: {}", request);

        Integer totalDataSuccess = 0;
        Integer totalDataFailed = 0;
        List<BillingCalculationErrorMessageDTO> errorMessageList = new ArrayList<>();

        Instant dateNow = Instant.now();
        String categoryUpperCase = request.getCategory().toUpperCase();
        String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());

        /* generate billing context date */
        BillingContextDate contextDate = getBillingContextDate(dateNow);

        /* get fee parameter VAT fee */
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameter.VAT.getValue());

        /* get all customer Core Type 1 */
        List<Customer> customerList = customerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

        /* calculate billing for all customers */
        for (Customer customer : customerList) {
            try {
                String customerCode = customer.getCustomerCode();
                BigDecimal customerMinimumFee = customer.getCustomerMinimumFee();
                BigDecimal customerSafekeepingFee = customer.getCustomerSafekeepingFee();
                BigDecimal transactionHandlingFee = customer.getCustomerTransactionHandling();
                String billingCategory = customer.getBillingCategory();
                String billingType = customer.getBillingType();
                String miCode = customer.getMiCode();

                /* get data investment management */
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(miCode);

                /* get data sk transaction */
                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get data rg daily */
                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* check and delete existing billing data with the same month and year */
                Optional<BillingCore> existingBillingCore = billingCoreRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(customerCode, billingCategory, billingType, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                if (existingBillingCore.isEmpty() || Boolean.TRUE.equals(!existingBillingCore.get().getPaid())) {
                    existingBillingCore.ifPresent(this::deleteExistingBillingCore);

                    CoreType2Parameter coreType2Parameter = new CoreType2Parameter(customerCode, skTransactionList, transactionHandlingFee, sfValRgDailyList, customerMinimumFee, vatFee);

                    CoreTemplate3 coreTemplate3 = calculationResult(coreType2Parameter, transactionHandlingFee, customerSafekeepingFee, vatFee);

                    BillingCore billingCore = buildBillingCore(contextDate, customer, investmentManagementDTO, coreTemplate3);
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

    private CoreTemplate3 calculationResult(CoreType2Parameter parameter, BigDecimal transactionHandlingFee, BigDecimal customerSafekeepingFee, BigDecimal vatFee) {
        Integer transactionHandlingValueFrequency = calculateTransactionValueFrequency(parameter.getCustomerCode(), parameter.getSkTransactionList());
        BigDecimal transactionHandlingAmountDue = calculateTransactionAmountDue(parameter.getCustomerCode(), transactionHandlingValueFrequency, parameter.getTransactionHandlingFee());
        BigDecimal safekeepingValueFrequency = calculateSafekeepingValueFrequency(parameter.getCustomerCode(), parameter.getSfValRgDailyList());
        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(parameter.getCustomerCode(), parameter.getCustomerMinimumFee(), parameter.getSfValRgDailyList());
        BigDecimal subTotalAmountDue = calculateSubTotalAmountDue(parameter.getCustomerCode(), transactionHandlingAmountDue, safekeepingAmountDue);
        BigDecimal vatAmountDue = calculateVATAmountDue(parameter.getCustomerCode(), subTotalAmountDue, parameter.getVatFee());
        BigDecimal totalAmountDue = calculateTotalAmountDue(parameter.getCustomerCode(), subTotalAmountDue, vatAmountDue);

        return CoreTemplate3.builder()
                .transactionHandlingValueFrequency(transactionHandlingValueFrequency)
                .transactionHandlingFee(transactionHandlingFee)
                .transactionHandlingAmountDue(transactionHandlingAmountDue)
                .safekeepingValueFrequency(safekeepingValueFrequency)
                .safekeepingFee(customerSafekeepingFee)
                .safekeepingAmountDue(safekeepingAmountDue)
                .subTotal(subTotalAmountDue)
                .vatFee(vatFee)
                .vatAmountDue(vatAmountDue)
                .totalAmountDue(totalAmountDue)
                .build();
    }

    private BillingCore buildBillingCore(BillingContextDate contextDate, Customer customer, InvestmentManagementDTO investmentManagementDTO, CoreTemplate3 coreTemplate3) {
        return BillingCore.builder()
                .createdAt(contextDate.getDateNow())
                .updatedAt(contextDate.getDateNow())
                .approvalStatus(ApprovalStatus.PENDING)
                .billingStatus(BillingStatus.GENERATED)
                .customerCode(customer.getCustomerCode())
                .customerName(customer.getCustomerName())
                .month(contextDate.getMonthNameMinus1())
                .year(contextDate.getYearMinus1())
                .billingPeriod(contextDate.getMonthNameMinus1() + " " + contextDate.getYearMinus1())
                .billingStatementDate(ConvertDateUtil.convertInstantToString(contextDate.getDateNow()))
                .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(contextDate.getDateNow()))
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
                .account(customer.getAccount())
                .accountName(customer.getAccountName())
                .currency(customer.getCurrency())
                .transactionHandlingValueFrequency(coreTemplate3.getTransactionHandlingValueFrequency())
                .transactionHandlingFee(coreTemplate3.getTransactionHandlingFee())
                .transactionHandlingAmountDue(coreTemplate3.getTransactionHandlingAmountDue())
                .safekeepingValueFrequency(coreTemplate3.getSafekeepingValueFrequency())
                .safekeepingFee(coreTemplate3.getSafekeepingFee())
                .safekeepingAmountDue(coreTemplate3.getSafekeepingAmountDue())
                .subTotal(coreTemplate3.getSubTotal())
                .vatFee(coreTemplate3.getVatFee())
                .vatAmountDue(coreTemplate3.getVatAmountDue())
                .totalAmountDue(coreTemplate3.getTotalAmountDue())
                .gefuCreated(false)
                .paid(false)
                .build();
    }

    private static BigDecimal calculateSafekeepingValueFrequency(String aid, List<SfValRgDaily> sfValRgDailyList) {
        List<SfValRgDaily> latestEntries = sfValRgDailyList.stream()
                .filter(entry -> entry.getDate().equals(sfValRgDailyList.stream()
                        .map(SfValRgDaily::getDate)
                        .max(Comparator.naturalOrder())
                        .orElse(null)))
                .toList();

        for (SfValRgDaily latestEntry : latestEntries) {
            log.info("Date '{}', Security Name '{}'", latestEntry.getDate(), latestEntry.getSecurityName());
        }

        BigDecimal safekeepingValueFrequency = latestEntries.stream()
                .map(SfValRgDaily::getMarketValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 2] Safekeeping value frequency Aid '{}' is '{}'", aid, safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private static BigDecimal calculateSafekeepingAmountDue(String aid, BigDecimal customerMinimumFee, List<SfValRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingAmountDue = sfValRgDailyList.stream()
                .map(SfValRgDaily::getEstimationSafekeepingFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        BigDecimal result = safekeepingAmountDue.compareTo(customerMinimumFee) < 0 ? customerMinimumFee : safekeepingAmountDue;

        log.info("[Core Type 2] Safekeeping amount due Aid '{}' is '{}'", aid, result);
        return result;
    }

    private static Integer calculateTransactionValueFrequency(String aid, List<SkTransaction> skTransactionList) {
        int totalTransactionHandling = skTransactionList.size();
        log.info("[Core Type 2] Total transaction handling Aid '{}' is '{}'", aid, totalTransactionHandling);
        return totalTransactionHandling;
    }

    private static BigDecimal calculateTransactionAmountDue(String aid, Integer transactionValueFrequency, BigDecimal transactionHandlingFee) {
        BigDecimal transactionHandlingAmountDue = transactionHandlingFee.multiply(new BigDecimal(transactionValueFrequency).setScale(0, RoundingMode.HALF_UP));
        log.info("[Core Type 2] Transaction handling amount due Aid '{}' is '{}'", aid, transactionHandlingAmountDue);
        return transactionHandlingAmountDue;
    }

    private static BigDecimal calculateSubTotalAmountDue(String aid, BigDecimal transactionHandlingAmountDue, BigDecimal safekeepingAmountDue) {
        BigDecimal subTotalAmountDue = transactionHandlingAmountDue.add(safekeepingAmountDue).setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 2] Sub total amount due Aid '{}' is '{}'", aid, subTotalAmountDue);
        return subTotalAmountDue;
    }


    private static BigDecimal calculateVATAmountDue(String aid, BigDecimal subTotalAmountDue, BigDecimal vatFee) {
        BigDecimal vatAmountDue = subTotalAmountDue
                .multiply(vatFee)
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 2] VAT amount due Aid '{}' is '{}'", aid, vatAmountDue);
        return vatAmountDue;
    }

    private static BigDecimal calculateTotalAmountDue(String aid, BigDecimal subTotalAmountDue, BigDecimal vatAmountDue) {
        BigDecimal totalAmountDue = subTotalAmountDue.add(vatAmountDue).setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 2] Total amount due Aid '{}' is '{}'", aid, totalAmountDue);
        return totalAmountDue;
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


    private void handleGeneralError(String string, Exception e, List<BillingCalculationErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add(e.getMessage());
        errorMessageList.add(new BillingCalculationErrorMessageDTO(string.isEmpty() ? "unknown" : string, validationErrors));
    }

    private void addErrorMessage(List<BillingCalculationErrorMessageDTO> calculationErrorMessages, String customerCode, String message) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(message);
        calculationErrorMessages.add(new BillingCalculationErrorMessageDTO(customerCode, errorMessages));
    }
}
