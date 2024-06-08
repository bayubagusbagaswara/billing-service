package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.billing.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.billing.BillingContextDate;
import com.bayu.billingservice.dto.core.CoreCalculateRequest;
import com.bayu.billingservice.dto.core.CoreTemplate7;
import com.bayu.billingservice.dto.core.CoreType3Parameter;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.GLCredit;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.BillingStatus;
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
public class CoreType3ServiceImpl implements CoreType3Service {

    private static final String GL_SAFEKEEPING_FEE = "GL Safekeeping Fee";

    private final BillingCoreRepository billingCoreRepository;
    private final CustomerService customerService;
    private final InvestmentManagementService investmentManagementService;
    private final SfValRgDailyService sfValRgDailyService;
    private final BillingNumberService billingNumberService;
    private final ConvertDateUtil convertDateUtil;
    private final GLCreditService glCreditService;

    @Override
    public BillingCalculationResponse calculate(CoreCalculateRequest request) {
        log.info("Start core billing calculation type 3 with a data request: {}", request);

        /* initialize response data */
        Integer totalDataSuccess = 0;
        Integer totalDataFailed = 0;
        List<BillingCalculationErrorMessageDTO> errorMessageList = new ArrayList<>();

        /* initialize data request */
        Instant dateNow = Instant.now();
        String categoryUpperCase = request.getCategory().toUpperCase();
        String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());

        /* generate billing context date */
        BillingContextDate contextDate = convertDateUtil.getBillingContextDate(dateNow);

        /* get all customer Core Type 3 */
        List<Customer> customerList = customerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

        for (Customer customer : customerList) {
            try {
                String customerCode = customer.getCustomerCode();
                String billingCategory = customer.getBillingCategory();
                String billingType = customer.getBillingType();
                String billingTemplate = customer.getBillingTemplate();
                String investmentManagementCode = customer.getMiCode();

                /* get data investment management */
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(investmentManagementCode);

                /* get data RG Daily */
                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get data GL Credit */
                GLCredit glCredit = glCreditService.getByBillingTemplateAndGLCreditName(billingTemplate, GL_SAFEKEEPING_FEE);

                /* check and delete existing billing data with the same month and year */
                Optional<BillingCore> existingBillingCore = billingCoreRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(customerCode, billingCategory, billingType, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                if (existingBillingCore.isEmpty() || Boolean.TRUE.equals(!existingBillingCore.get().getPaid())) {
                    existingBillingCore.ifPresent(this::deleteExistingBillingCore);

                    CoreType3Parameter coreType3Parameter = new CoreType3Parameter(sfValRgDailyList);

                    CoreTemplate7 coreTemplate7 = calculationResult(coreType3Parameter, customer, glCredit);

                    BillingCore billingCore = buildBillingCore(contextDate, customer, investmentManagementDTO, coreTemplate7);

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

    private static BillingCore buildBillingCore(BillingContextDate contextDate, Customer customer, InvestmentManagementDTO investmentManagementDTO, CoreTemplate7 coreTemplate7) {
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
                .accountCostCenterDebit(coreTemplate7.getAccountCostCenterDebit())
                .safekeepingValueFrequency(coreTemplate7.getSafekeepingValueFrequency())
                .safekeepingFee(coreTemplate7.getSafekeepingFee())
                .safekeepingAmountDue(coreTemplate7.getSafekeepingAmountDue())
                .safekeepingJournal(coreTemplate7.getSafekeepingJournal())
                .gefuCreated(false)
                .paid(false)
                .build();
    }

    private CoreTemplate7 calculationResult(CoreType3Parameter parameter, Customer customer, GLCredit glCredit) {
        String account = customer.getAccount();
        String costCenterDebit = customer.getDebitTransfer();
        String accountCostCenterDebit = "GL " + account + " CC " + costCenterDebit;

        int glCreditAccountValue = glCredit.getGlCreditAccountValue();
        String costCenter = customer.getCostCenter();
        String glAccountCostCenterCredit = "GL " + glCreditAccountValue + " CC " + costCenter;

        BigDecimal safekeepingValueFrequency = calculateSafekeepingValueFrequency(customer.getCustomerCode(), parameter.getSfValRgDailyList());
        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(customer.getCustomerCode(), parameter.getSfValRgDailyList());

        return CoreTemplate7.builder()
                .accountCostCenterDebit(accountCostCenterDebit)
                .safekeepingValueFrequency(safekeepingValueFrequency)
                .safekeepingFee(customer.getCustomerSafekeepingFee())
                .safekeepingAmountDue(safekeepingAmountDue)
                .safekeepingJournal(glAccountCostCenterCredit)
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

        log.info("[Core Type 3] Safekeeping value frequency Aid '{}' is '{}'", aid, safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private static BigDecimal calculateSafekeepingAmountDue(String aid, List<SfValRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingAmountDue = sfValRgDailyList.stream()
                .map(SfValRgDaily::getEstimationSafekeepingFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 3] Safekeeping amount due Aid '{}' is '{}'", aid, safekeepingAmountDue);
        return safekeepingAmountDue;
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
