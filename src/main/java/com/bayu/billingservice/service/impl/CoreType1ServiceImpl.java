package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.billing.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.core.CoreCalculateRequest;
import com.bayu.billingservice.dto.billing.BillingContextDate;
import com.bayu.billingservice.dto.core.CoreTemplate3;
import com.bayu.billingservice.dto.core.CoreType1Parameter;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.model.*;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.BillingStatus;
import com.bayu.billingservice.repository.BillingCoreRepository;
import com.bayu.billingservice.service.*;
import com.bayu.billingservice.util.ConvertDateUtil;
import com.bayu.billingservice.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.bayu.billingservice.model.enumerator.FeeParameter.VAT;

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

    @Transactional
    @Override
    public synchronized BillingCalculationResponse calculate(CoreCalculateRequest request) {
        /* initialize data response */
        Integer totalDataSuccess = 0;
        Integer totalDataFailed = 0;
        List<BillingCalculationErrorMessageDTO> errorMessageList = new ArrayList<>();

        /* initialize data request */
        Instant dateNow = Instant.now();
        String categoryUpperCase = request.getCategory().toUpperCase();
        String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());

        /* generate billing context date */
        BillingContextDate contextDate = convertDateUtil.getBillingContextDate(dateNow);

        /* get fee parameter VAT fee */
        BigDecimal vatFee = feeParameterService.getValueByName(VAT.getValue());

        /* get all customer Core Type 1 */
        List<Customer> customerList = customerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

        /* calculate billing for all customers */
        for (Customer customer : customerList) {
            try {
                String customerCode = customer.getCustomerCode();

                /* get data investment management */
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customer.getMiCode());

                /* get data sk transaction */
                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get data rg daily */
                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(customerCode, contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* get billing data to check whether the data is in the database or not */
                Optional<BillingCore> existingBillingCore = billingCoreRepository.findByCustomerCodeAndSubCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(
                        customerCode, customer.getSubCode(), customer.getBillingCategory(), customer.getBillingType(), contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

                /* check paid status. if it is FALSE, it can be regenerated */
                if (existingBillingCore.isEmpty() || Boolean.TRUE.equals(!existingBillingCore.get().getPaid())) {

                    /* delete billing data if it exists in the database */
                    existingBillingCore.ifPresent(this::deleteExistingBillingCore);

                    /* create billing core */
                    BillingCore billingCore = buildBillingCore(contextDate, customer, investmentManagementDTO);

                    /* create type 1 parameter */
                    CoreType1Parameter coreType1Parameter = new CoreType1Parameter(skTransactionList, customer.getCustomerTransactionHandling(), sfValRgDailyList, vatFee);

                    /* calculation billing */
                    CoreTemplate3 coreTemplate3 = calculationResult(coreType1Parameter, customer.getCustomerTransactionHandling(), customer.getCustomerSafekeepingFee(), vatFee);

                    /* update billing core data to include calculated values*/
                    updateBillingCoreForCoreTemplate3(billingCore, coreTemplate3);

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

        log.info("Total successful calculations: {}, total failed calculations: {}", totalDataSuccess, totalDataFailed);
        return new BillingCalculationResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public List<BillingCore> getAll() {
        String type = "TYPE_1";
        return billingCoreRepository.findAllByType(type);
    }

    private Integer calculateTransactionHandlingValueFrequency(List<SkTransaction> skTransactionList) {
        Integer totalTransactionHandling = skTransactionList.size();
        log.info("[Core Type 1] Total transaction handling: {}", totalTransactionHandling);
        return totalTransactionHandling;
    }

    private BigDecimal calculateTransactionHandlingAmountDue(BigDecimal transactionHandlingFee, int transactionHandlingValueFrequency) {
        BigDecimal transactionHandlingAmountDue = transactionHandlingFee.multiply(new BigDecimal(transactionHandlingValueFrequency).setScale(0, RoundingMode.HALF_UP));
        log.info("[Core Type 1] Transaction handling amount due: {}", transactionHandlingAmountDue);
        return transactionHandlingAmountDue;
    }

    private BigDecimal calculateSafekeepingValueFrequency(List<SfValRgDaily> sfValRgDailyList) {
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

        log.info("[Core Type 1] Safekeeping value frequency: {}", safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private BigDecimal calculateSafekeepingAmountDue(List<SfValRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingAmountDue = sfValRgDailyList.stream()
                .map(SfValRgDaily::getEstimationSafekeepingFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 1] Safekeeping amount due: {}", safekeepingAmountDue);
        return safekeepingAmountDue;
    }

    private BigDecimal calculateSubTotalAmountDue(BigDecimal transactionHandlingAmountDue, BigDecimal safekeepingAmountDue) {
        BigDecimal subTotalAmountDue = transactionHandlingAmountDue.add(safekeepingAmountDue).setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 1] Sub total amount due: {}", subTotalAmountDue);
        return subTotalAmountDue;
    }

    private BigDecimal calculateVATAmountDue(BigDecimal subTotalAmountDue, BigDecimal vatFee) {
        BigDecimal vatAmountDue = subTotalAmountDue
                .multiply(vatFee)
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 1] VAT amount due: {}", vatAmountDue);
        return vatAmountDue;
    }

    private BigDecimal calculateTotalAmountDue(BigDecimal subTotalAmountDue, BigDecimal vatAmountDue) {
        BigDecimal totalAmountDue = subTotalAmountDue.add(vatAmountDue).setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 1] Total amount due: {}", totalAmountDue);
        return totalAmountDue;
    }

    private CoreTemplate3 calculationResult(CoreType1Parameter parameter, BigDecimal transactionHandlingFee, BigDecimal customerSafekeepingFee, BigDecimal vatFee) {
        Integer transactionHandlingValueFrequency = calculateTransactionHandlingValueFrequency(parameter.getSkTransactionList());
        BigDecimal transactionHandlingAmountDue = calculateTransactionHandlingAmountDue(transactionHandlingFee, transactionHandlingValueFrequency);
        BigDecimal safekeepingValueFrequency = calculateSafekeepingValueFrequency(parameter.getSfValRgDailyList());
        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(parameter.getSfValRgDailyList());
        BigDecimal subTotal = calculateSubTotalAmountDue(transactionHandlingAmountDue, safekeepingAmountDue);
        BigDecimal vatAmountDue = calculateVATAmountDue(subTotal, vatFee);
        BigDecimal totalAmountDue = calculateTotalAmountDue(subTotal, vatAmountDue);

        return CoreTemplate3.builder()
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
                .build();
    }

    private BillingCore buildBillingCore(BillingContextDate contextDate, Customer customer, InvestmentManagementDTO investmentManagementDTO) {
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
                .gefuCreated(false)
                .paid(false)
                .build();
    }

    private void updateBillingCoreForCoreTemplate3(BillingCore billingCore, CoreTemplate3 coreTemplate3) {
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
