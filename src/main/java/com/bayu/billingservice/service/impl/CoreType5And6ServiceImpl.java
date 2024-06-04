package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.billing.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.billing.BillingContextDate;
import com.bayu.billingservice.dto.core.CoreCalculateRequest;
import com.bayu.billingservice.dto.core.CoreTemplate1;
import com.bayu.billingservice.dto.core.CoreTemplate4;
import com.bayu.billingservice.dto.core.CoreType5And6Parameter;
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
@Slf4j
@RequiredArgsConstructor
public class CoreType5And6ServiceImpl implements CoreType5And6Service {

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
        log.info("Start core billing calculation type 5 and 6 with a data request: {}", request);
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
        BigDecimal kseiTransactionFee = feeParameterService.getValueByName(FeeParameter.KSEI.getValue());
        BigDecimal bis4TransactionFee = feeParameterService.getValueByName(FeeParameter.BI_SSSS.getValue());
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameter.VAT.getValue());

        /* get all customer Core Type 3 */
        List<Customer> customerList = customerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

        for (Customer customer : customerList) {
            try {
                String customerCode = customer.getCustomerCode();
                String kseiSafeCode = customer.getKseiSafeCode();
                String investmentManagementCode = customer.getMiCode();

                /* get data investment management */
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(investmentManagementCode);
                /* get data sk transaction */
                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get data RG Daily */
                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get data KSEI safekeeping fee */
                BigDecimal kseiSafekeepingFeeAmount = kseiSafekeepingFeeService.calculateAmountFeeByKseiSafeCodeAndMonthAndYear(kseiSafeCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* check and delete existing billing data with the same month and year */
                Optional<BillingCore> existingBillingCore = billingCoreRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(
                        customerCode, customer.getBillingCategory(), customer.getBillingType(), contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                if (existingBillingCore.isEmpty() || Boolean.TRUE.equals(!existingBillingCore.get().getPaid())) {

                    existingBillingCore.ifPresent(this::deleteExistingBillingCore);

                    CoreType5And6Parameter coreType5And6Parameter = new CoreType5And6Parameter(
                            customer.getCustomerSafekeepingFee(), kseiSafekeepingFeeAmount, kseiTransactionFee, bis4TransactionFee, sfValRgDailyList, skTransactionList, vatFee);

                    BillingCore billingCore = createBillingCore(contextDate, customer, investmentManagementDTO);

                    if (BillingTemplate.CORE_TEMPLATE_1.getValue().equalsIgnoreCase(customer.getBillingTemplate())) {
                        CoreTemplate1 coreTemplate1 = calculateWithoutNPWP(coreType5And6Parameter);
                        updateBillingCoreForTemplate1(billingCore, coreTemplate1);
                    } else if (BillingTemplate.CORE_TEMPLATE_4.getValue().equalsIgnoreCase(customer.getBillingTemplate())) {
                        CoreTemplate4 coreTemplate4 = calculateWithNPWP(coreType5And6Parameter);
                        updateBillingCoreForTemplate4(billingCore, coreTemplate4);
                    }

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
        log.info("Total successfully calculations: {}, total failed calculations: {}", totalDataSuccess, totalDataFailed);
        return new BillingCalculationResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private void updateBillingCoreForTemplate4(BillingCore billingCore, CoreTemplate4 coreTemplate4) {
        billingCore.setSafekeepingValueFrequency(coreTemplate4.getSafekeepingValueFrequency());
        billingCore.setSafekeepingFee(coreTemplate4.getSafekeepingFee());
        billingCore.setSafekeepingAmountDue(coreTemplate4.getSafekeepingAmountDue());
        billingCore.setBis4TransactionValueFrequency(coreTemplate4.getBis4TransactionValueFrequency());
        billingCore.setBis4TransactionFee(coreTemplate4.getBis4TransactionFee());
        billingCore.setBis4TransactionAmountDue(coreTemplate4.getBis4TransactionAmountDue());
        billingCore.setSubTotal(coreTemplate4.getSubTotal());
        billingCore.setVatFee(coreTemplate4.getVatFee());
        billingCore.setVatAmountDue(coreTemplate4.getVatAmountDue());
        billingCore.setKseiTransactionValueFrequency(coreTemplate4.getKseiTransactionValueFrequency());
        billingCore.setKseiTransactionFee(coreTemplate4.getKseiTransactionFee());
        billingCore.setKseiTransactionAmountDue(coreTemplate4.getKseiTransactionAmountDue());
        billingCore.setKseiSafekeepingAmountDue(coreTemplate4.getKseiSafekeepingAmountDue());
        billingCore.setTotalAmountDue(coreTemplate4.getTotalAmountDue());
    }

    private void updateBillingCoreForTemplate1(BillingCore billingCore, CoreTemplate1 coreTemplate1) {
        billingCore.setSafekeepingValueFrequency(coreTemplate1.getSafekeepingValueFrequency());
        billingCore.setSafekeepingFee(coreTemplate1.getSafekeepingFee());
        billingCore.setSafekeepingAmountDue(coreTemplate1.getSafekeepingAmountDue());
        billingCore.setKseiTransactionValueFrequency(coreTemplate1.getKseiTransactionValueFrequency());
        billingCore.setKseiTransactionFee(coreTemplate1.getKseiTransactionFee());
        billingCore.setKseiTransactionAmountDue(coreTemplate1.getKseiTransactionAmountDue());
        billingCore.setBis4TransactionValueFrequency(coreTemplate1.getBis4TransactionValueFrequency());
        billingCore.setBis4TransactionFee(coreTemplate1.getBis4TransactionFee());
        billingCore.setBis4TransactionAmountDue(coreTemplate1.getBis4TransactionAmountDue());
        billingCore.setKseiSafekeepingAmountDue(coreTemplate1.getKseiSafekeepingAmountDue());
        billingCore.setTotalAmountDue(coreTemplate1.getTotalAmountDue());
    }

    private BillingCore createBillingCore(BillingContextDate contextDate, Customer customer, InvestmentManagementDTO investmentManagementDTO) {
        Instant dateNow = contextDate.getDateNow();
        return BillingCore.builder()
                .createdAt(dateNow)
                .updatedAt(dateNow)
                .approvalStatus(ApprovalStatus.PENDING)
                .billingStatus(BillingStatus.GENERATED)
                .customerCode(customer.getCustomerCode())
                .subCode(customer.getSubCode())
                .customerName(customer.getCustomerName())
                .month(contextDate.getMonthNameMinus1())
                .year(contextDate.getYearMinus1())
                .billingPeriod(contextDate.getMonthNameMinus1() + " " + contextDate.getYearMinus1())
                .billingStatementDate(ConvertDateUtil.convertInstantToString(dateNow))
                .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(dateNow))
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
        log.info("Safekeeping value frequency: {}", safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private static BigDecimal calculateSafekeepingAmountDue(List<SfValRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingAmountDue = sfValRgDailyList.stream()
                .map(SfValRgDaily::getEstimationSafekeepingFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("Safekeeping amount due: {}", safekeepingAmountDue);
        return safekeepingAmountDue;
    }

    private static BigDecimal calculateTotalAmountDueWithoutNPWP(BigDecimal safekeepingAmountDue, BigDecimal kseiAmountFee) {
        BigDecimal totalAmountDue = safekeepingAmountDue.add(kseiAmountFee);
        log.info("Total amount due without NPWP: {}", totalAmountDue);
        return totalAmountDue;
    }

    private CoreTemplate1 calculateWithoutNPWP(CoreType5And6Parameter param) {
        int[] filteredTransactionsType = skTransactionService.filterTransactionsType(param.getSkTransactionList());
        int transactionCBESTTotal = filteredTransactionsType[0];
        int transactionBISSSSTotal = filteredTransactionsType[1];

        BigDecimal safekeepingValueFrequency = calculateSafekeepingValueFrequency(param.getSfValRgDailyList());
        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(param.getSfValRgDailyList());
        BigDecimal kseiTransactionAmountDue = calculateKseiTransactionAmountDue(transactionCBESTTotal, param.getKseiTransactionFee());
        BigDecimal bis4TransactionAmountDue = calculateBis4TransactionAmountDue(transactionBISSSSTotal, param.getBis4TransactionFee());
        BigDecimal totalAmountDue = calculateTotalAmountDueWithoutNPWP(safekeepingAmountDue, param.getKseiSafekeepingFeeAmount());

        return CoreTemplate1.builder()
                .safekeepingValueFrequency(safekeepingValueFrequency)
                .safekeepingFee(param.getCustomerSafekeepingFee())
                .safekeepingAmountDue(safekeepingAmountDue)
                .kseiTransactionValueFrequency(transactionCBESTTotal)
                .kseiTransactionFee(param.getKseiTransactionFee())
                .kseiTransactionAmountDue(kseiTransactionAmountDue)
                .bis4TransactionValueFrequency(transactionBISSSSTotal)
                .bis4TransactionFee(param.getBis4TransactionFee())
                .bis4TransactionAmountDue(bis4TransactionAmountDue)
                .kseiSafekeepingAmountDue(param.getKseiSafekeepingFeeAmount())
                .totalAmountDue(totalAmountDue)
                .build();
    }

    private static BigDecimal calculateKseiTransactionAmountDue(Integer transactionCBESTTotal, BigDecimal kseiTransactionFee) {
        BigDecimal kseiTransactionAmountDue = new BigDecimal(transactionCBESTTotal)
                .multiply(kseiTransactionFee).setScale(0, RoundingMode.HALF_UP);
        log.info("KSEI transaction amount due: {}", kseiTransactionAmountDue);
        return kseiTransactionAmountDue;
    }

    private static BigDecimal calculateBis4TransactionAmountDue(int transactionBISSSSTotal, BigDecimal bis4TransactionFee) {
        BigDecimal bis4TransactionAmountDue = new BigDecimal(transactionBISSSSTotal)
                .multiply(bis4TransactionFee).setScale(0, RoundingMode.HALF_UP);
        log.info("BI-SSSS transaction amount due: {}", bis4TransactionAmountDue);
        return bis4TransactionAmountDue;
    }

    private static BigDecimal calculateSubTotalAmountDue(BigDecimal safekeepingAmountDue, BigDecimal bis4TransactionAmountDue) {
        BigDecimal subTotalAmountDue = safekeepingAmountDue.add(bis4TransactionAmountDue);
        log.info("Sub total amount due: {}", subTotalAmountDue);
        return subTotalAmountDue;
    }

    private static BigDecimal calculateVATAmountDue(BigDecimal vatFee, BigDecimal subTotalAmountDue) {
        BigDecimal vatAmountDue = subTotalAmountDue
                .multiply(vatFee)
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("VAT amount due: {}", vatAmountDue);
        return vatAmountDue;
    }

    private static BigDecimal calculateTotalAmountDueWithNPWP(BigDecimal subTotalAmountDue, BigDecimal vatAmountDue, BigDecimal kseiTransactionAmountDue, BigDecimal kseiAmountFee) {
        BigDecimal totalAmountDue = subTotalAmountDue
                .add(vatAmountDue)
                .add(kseiTransactionAmountDue)
                .add(kseiAmountFee);
        log.info("Total amount due with NPWP: {}", totalAmountDue);
        return totalAmountDue;
    }

    private CoreTemplate4 calculateWithNPWP(CoreType5And6Parameter param) {
        int[] filteredTransactionsType = skTransactionService.filterTransactionsType(param.getSkTransactionList());
        int transactionCBESTTotal = filteredTransactionsType[0];
        int transactionBISSSSTotal = filteredTransactionsType[1];

        BigDecimal safekeepingValueFrequency = calculateSafekeepingValueFrequency(param.getSfValRgDailyList());
        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(param.getSfValRgDailyList());
        BigDecimal kseiTransactionAmountDue = calculateKseiTransactionAmountDue(transactionCBESTTotal, param.getKseiTransactionFee());
        BigDecimal bis4TransactionAmountDue = calculateBis4TransactionAmountDue(transactionBISSSSTotal, param.getBis4TransactionFee());
        BigDecimal subTotalAmountDue = calculateSubTotalAmountDue(safekeepingAmountDue, bis4TransactionAmountDue);
        BigDecimal vatAmountDue = calculateVATAmountDue(param.getVatFee(), subTotalAmountDue);
        BigDecimal totalAmountDue = calculateTotalAmountDueWithNPWP(subTotalAmountDue, vatAmountDue, kseiTransactionAmountDue, param.getKseiSafekeepingFeeAmount());

        return CoreTemplate4.builder()
                .safekeepingValueFrequency(safekeepingValueFrequency)
                .safekeepingFee(param.getCustomerSafekeepingFee())
                .safekeepingAmountDue(safekeepingAmountDue)
                .bis4TransactionValueFrequency(transactionBISSSSTotal)
                .bis4TransactionFee(param.getBis4TransactionFee())
                .bis4TransactionAmountDue(bis4TransactionAmountDue)
                .subTotal(subTotalAmountDue)
                .vatFee(param.getVatFee())
                .vatAmountDue(vatAmountDue)
                .kseiTransactionValueFrequency(transactionCBESTTotal)
                .kseiTransactionFee(param.getKseiTransactionFee())
                .kseiTransactionAmountDue(kseiTransactionAmountDue)
                .kseiSafekeepingAmountDue(param.getKseiSafekeepingFeeAmount())
                .totalAmountDue(totalAmountDue)
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
