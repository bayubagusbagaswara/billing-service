package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.billing.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.billing.BillingContextDate;
import com.bayu.billingservice.dto.core.CoreCalculateRequest;
import com.bayu.billingservice.dto.core.CoreTemplate3;
import com.bayu.billingservice.dto.core.CoreTemplate5;
import com.bayu.billingservice.dto.core.CoreType4Parameter;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SkTransaction;
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

import static com.bayu.billingservice.model.enumerator.BillingTemplate.CORE_TEMPLATE_3;
import static com.bayu.billingservice.model.enumerator.BillingTemplate.CORE_TEMPLATE_5;
import static com.bayu.billingservice.model.enumerator.FeeParameter.KSEI;
import static com.bayu.billingservice.model.enumerator.FeeParameter.VAT;

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
        BillingContextDate contextDate = convertDateUtil.getBillingContextDate(dateNow);

        /* get data fee parameters */
        BigDecimal kseiTransactionFee = feeParameterService.getValueByName(KSEI.getValue());
        BigDecimal vatFee = feeParameterService.getValueByName(VAT.getValue());

        /* get all customer Core Type 3 */
        List<Customer> customerList = customerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

        for (Customer customer : customerList) {
            try {
                String customerCode = customer.getCustomerCode();

                /* get data investment management */
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customer.getMiCode());

                /* get data sk transaction */
                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get data RG Daily */
                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get data KSEI safekeeping fee */
                BigDecimal kseiSafeFeeAmount = kseiSafekeepingFeeService.calculateAmountFeeByKseiSafeCodeAndMonthAndYear(customer.getKseiSafeCode(), contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get billing data to check whether the data is in the database or not */
                Optional<BillingCore> existingBillingCore = billingCoreRepository.findByCustomerCodeAndSubCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(
                        customerCode, customer.getSubCode(), customer.getBillingCategory(), customer.getBillingType(), contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* check paid status. if it is FALSE, it can be regenerated */
                if (existingBillingCore.isEmpty() || Boolean.TRUE.equals(!existingBillingCore.get().getPaid())) {

                    /* delete billing data if it exists in the database */
                    existingBillingCore.ifPresent(this::deleteExistingBillingCore);

                    /* create core type 4 parameter */
                    CoreType4Parameter coreType4Parameter = new CoreType4Parameter(
                            customer.getCustomerSafekeepingFee(), customer.getCustomerTransactionHandling(), vatFee,
                            sfValRgDailyList, skTransactionList, kseiTransactionFee, kseiSafeFeeAmount);

                    /* create billing core */
                    BillingCore billingCore = createBillingCore(contextDate, customer, investmentManagementDTO);

                    /* check template, because type 4 has a different template */
                    if (CORE_TEMPLATE_5.getValue().equalsIgnoreCase(customer.getBillingTemplate())) {
                        /* calculation billing for EB */
                        CoreTemplate5 coreTemplate5 = calculateEB(coreType4Parameter);
                        /* update billing core data to include calculated values */
                        updateBillingCoreForTemplate5(billingCore, coreTemplate5);
                    } else if (CORE_TEMPLATE_3.getValue().equalsIgnoreCase(customer.getBillingTemplate())) {
                        /* calculation billing for ITAMA */
                        CoreTemplate3 coreTemplate3 = calculateITAMA(coreType4Parameter);
                        /* update billing core data to include calculated values */
                        updateBillingCoreForTemplate3(billingCore, coreTemplate3);
                    }

                    /* create a billing number then set it to the billing core */
                    String number = billingNumberService.generateSingleNumber(contextDate.getMonthNameNow(), contextDate.getYearNow());
                    billingCore.setBillingNumber(number);

                    /* save to the database */
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
        log.info("Total successfully calculations: {}, total failed calculations: {}", totalDataSuccess, totalDataFailed);
        return new BillingCalculationResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private BillingCore createBillingCore(BillingContextDate contextDate, Customer customer, InvestmentManagementDTO investmentManagementDTO) {
        return BillingCore.builder()
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
                .paid(false)
                .gefuCreated(false)
                .build();
    }

    private void updateBillingCoreForTemplate5(BillingCore billingCore, CoreTemplate5 coreTemplate5) {
        billingCore.setKseiTransactionValueFrequency(coreTemplate5.getKseiTransactionValueFrequency());
        billingCore.setKseiTransactionFee(coreTemplate5.getKseiTransactionFee());
        billingCore.setKseiTransactionAmountDue(coreTemplate5.getKseiTransactionAmountDue());
        billingCore.setKseiSafekeepingAmountDue(coreTemplate5.getKseiSafekeepingAmountDue());
        billingCore.setTotalAmountDue(coreTemplate5.getTotalAmountDue());
    }

    private void updateBillingCoreForTemplate3(BillingCore billingCore, CoreTemplate3 coreTemplate3) {
        billingCore.setTransactionHandlingValueFrequency(coreTemplate3.getTransactionHandlingValueFrequency());
        billingCore.setTransactionHandlingFee(coreTemplate3.getTransactionHandlingFee());
        billingCore.setTransactionHandlingAmountDue(coreTemplate3.getTransactionHandlingAmountDue());
        billingCore.setSafekeepingValueFrequency(coreTemplate3.getSafekeepingValueFrequency());
        billingCore.setSafekeepingFee(coreTemplate3.getSafekeepingFee());
        billingCore.setSafekeepingAmountDue(coreTemplate3.getSafekeepingAmountDue());
        billingCore.setSubTotal(coreTemplate3.getSubTotal());
        billingCore.setVatFee(coreTemplate3.getVatFee());
        billingCore.setVatAmountDue(coreTemplate3.getVatAmountDue());
        billingCore.setTotalAmountDue(coreTemplate3.getTotalAmountDue());
    }

    private static BigDecimal calculateSafekeepingValueFrequency(List<SfValRgDaily> sfValRgDailyList) {
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

        log.info("[Core Type 4 ITAMA] Safekeeping value frequency: {}", safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private static BigDecimal calculateSafekeepingAmountDue(List<SfValRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingAmountDue = sfValRgDailyList.stream()
                .map(SfValRgDaily::getEstimationSafekeepingFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 4 ITAMA] Safekeeping amount due: {}", safekeepingAmountDue);
        return safekeepingAmountDue;
    }

    private static BigDecimal calculateVatAmountDue(BigDecimal subTotalAmountDue, BigDecimal vatFee) {
        BigDecimal vatAmountDue = subTotalAmountDue.multiply(vatFee)
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 4 ITAMA] VAT amount duE: {}", vatAmountDue);
        return vatAmountDue;
    }

    private static BigDecimal calculateTotalAmountDueITAMA(BigDecimal safekeepingAmountDue, BigDecimal vatAmountDue) {
        BigDecimal totalAmountDueITAMA = safekeepingAmountDue.add(vatAmountDue);
        log.info("[Core Type 4 ITAMA] Total amount due: {}", vatAmountDue);
        return totalAmountDueITAMA;
    }

    private static CoreTemplate3 calculateITAMA(CoreType4Parameter param) {
        BigDecimal safekeepingValueFrequency = calculateSafekeepingValueFrequency(param.getSfValRgDailyList());
        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(param.getSfValRgDailyList());
        BigDecimal vatAmountDue = calculateVatAmountDue(safekeepingAmountDue, param.getVatFee());
        BigDecimal totalAmountDueITAMA = calculateTotalAmountDueITAMA(safekeepingAmountDue, vatAmountDue);

        Integer transactionHandlingFrequency = 0;
        BigDecimal transactionHandlingAmountDue = BigDecimal.ZERO;
        BigDecimal subTotal = transactionHandlingAmountDue.add(safekeepingAmountDue);

        return CoreTemplate3.builder()
                .transactionHandlingValueFrequency(transactionHandlingFrequency)
                .transactionHandlingFee(param.getTransactionHandlingFee())
                .transactionHandlingAmountDue(transactionHandlingAmountDue)
                .safekeepingValueFrequency(safekeepingValueFrequency)
                .safekeepingFee(param.getCustomerSafekeepingFee())
                .safekeepingAmountDue(safekeepingAmountDue)
                .subTotal(subTotal)
                .vatFee(param.getVatFee())
                .vatAmountDue(vatAmountDue)
                .totalAmountDue(totalAmountDueITAMA)
                .build();
    }

    private static int calculateTransactionHandlingValueFrequency(List<SkTransaction> skTransactionList) {
        int totalTransactionHandling = skTransactionList.size();
        log.info("[Core Type 4 EB] Total transaction handling: {}", totalTransactionHandling);
        return totalTransactionHandling;
    }

    private static BigDecimal calculateKSEITransactionAmountDue(BigDecimal kseiTransactionFee, Integer transactionValueFrequency) {
        BigDecimal kseiTransactionAmountDue = kseiTransactionFee.multiply(new BigDecimal(transactionValueFrequency)).setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 4 EB] KSEI transaction amount due: {}", kseiTransactionAmountDue);
        return kseiTransactionAmountDue;
    }

    private static BigDecimal calculateTotalAmountDueEB(BigDecimal kseiSafekeepingAmountDue, BigDecimal kseiTransactionAmountDue) {
        BigDecimal totalAmountDueEB = kseiSafekeepingAmountDue.add(kseiTransactionAmountDue);
        log.info("[Core Type 4 EB] Total amount due: {}", totalAmountDueEB);
        return totalAmountDueEB;
    }

    private static CoreTemplate5 calculateEB(CoreType4Parameter param) {
        int kseiTransactionValueFrequency = calculateTransactionHandlingValueFrequency(param.getSkTransactionList());
        BigDecimal kseiTransactionAmountDue = calculateKSEITransactionAmountDue(param.getKseiTransactionFee(), kseiTransactionValueFrequency);
        BigDecimal totalAmountDueEB = calculateTotalAmountDueEB(param.getKseiSafeFeeAmount(), kseiTransactionAmountDue);

        return CoreTemplate5.builder()
                .kseiTransactionValueFrequency(kseiTransactionValueFrequency)
                .kseiTransactionFee(param.getKseiTransactionFee())
                .kseiTransactionAmountDue(kseiTransactionAmountDue)
                .kseiSafekeepingAmountDue(param.getKseiSafeFeeAmount())
                .totalAmountDue(totalAmountDueEB)
                .build();
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
