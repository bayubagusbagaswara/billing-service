package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class Core1CalculateServiceImpl implements Core1CalculateService {

    private final FeeParameterService feeParameterService;
    private final SkTransactionService skTransactionService;
    private final SfValRgDailyService sfValRgDailyService;
    private final BillingNumberService billingNumberService;
    private final BillingCoreRepository billingCoreRepository;
    private final ConvertDateUtil convertDateUtil;
    private final CustomerService customerService;

    @Override
    public String calculate(CoreCalculateRequest request) {
        log.info("Start calculate Billing Core type 1 with request : {}", request);
        try {
            String categoryUpperCase = request.getCategory().toUpperCase();
            String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());

            Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
            String monthName = monthMinus1.get("monthName");
            int year = Integer.parseInt(monthMinus1.get("year"));

            // Initialization variable
            int transactionHandlingValueFrequency;
            BigDecimal transactionHandlingAmountDue;
            BigDecimal safekeepingValueFrequency;
            BigDecimal safekeepingAmountDue;
            BigDecimal subTotal;
            BigDecimal vatAmountDue;
            BigDecimal totalAmountDue;
            Instant dateNow = Instant.now();
            int totalDataSuccess = 0;

            BigDecimal vatFee = feeParameterService.getValueByName(FeeParameter.VAT.name());

            List<Customer> billingCustomerList = customerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

            for (BillingCustomer billingCustomer : billingCustomerList) {
                String aid = billingCustomer.getCustomerCode();
                BigDecimal customerMinimumFee = billingCustomer.getCustomerMinimumFee();
                BigDecimal customerSafekeepingFee = billingCustomer.getCustomerSafekeepingFee();
                BigDecimal transactionHandlingFee = billingCustomer.getCustomerTransactionHandling();
                String billingCategory = billingCustomer.getBillingCategory();
                String billingType = billingCustomer.getBillingType();
                String miCode = billingCustomer.getMiCode();

                // check and delete existing billing data with the same month and year
                coreGeneralService.checkingExistingBillingCore(monthName, year, aid, billingCategory, billingType);

                InvestmentManagementDTO billingMIDTO = billingMIService.getByCode(miCode);

                List<SkTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);

                transactionHandlingValueFrequency = calculateTransactionHandlingValueFrequency(aid, skTransactionList);

                transactionHandlingAmountDue = calculateTransactionHandlingAmountDue(aid, transactionHandlingFee, transactionHandlingValueFrequency);

                safekeepingValueFrequency = calculateSafekeepingValueFrequency(aid, sfValRgDailyList);

                safekeepingAmountDue = calculateSafekeepingAmountDue(aid, sfValRgDailyList);

                subTotal = calculateSubTotalAmountDue(aid, transactionHandlingAmountDue, safekeepingAmountDue);

                vatAmountDue = calculateVatAmountDue(aid, subTotal, vatFee);

                totalAmountDue = calculateTotalAmountDue(aid, subTotal, vatAmountDue);

                BillingCore billingCore = BillingCore.builder()
                        .createdAt(dateNow)
                        .updatedAt(dateNow)
                        .approvalStatus(ApprovalStatus.PENDING)
                        .billingStatus(BillingStatus.Generated)
                        .customerCode(billingCustomer.getCustomerCode())
                        .customerName(billingCustomer.getCustomerName())
                        .month(monthName)
                        .year(year)
                        .billingPeriod(monthName + " " + year)
                        .billingStatementDate(ConvertDateUtil.convertInstantToString(dateNow))
                        .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(dateNow))
                        .billingCategory(billingCustomer.getBillingCategory())
                        .billingType(billingCustomer.getBillingType())
                        .billingTemplate(billingCustomer.getBillingTemplate())
                        .investmentManagementName(billingMIDTO.getName())
                        .investmentManagementAddress1(billingMIDTO.getAddress1())
                        .investmentManagementAddress2(billingMIDTO.getAddress2())
                        .investmentManagementAddress3(billingMIDTO.getAddress3())
                        .investmentManagementAddress4(billingMIDTO.getAddress4())
                        .investmentManagementEmail(billingMIDTO.getEmail())
                        .investmentManagementUniqueKey(billingMIDTO.getUniqueKey())

                        .customerMinimumFee(customerMinimumFee)
                        .customerSafekeepingFee(customerSafekeepingFee)

                        .gefuCreated(false)
                        .paid(false)
                        .accountName(billingCustomer.getAccountName())
                        .account(billingCustomer.getAccount())
                        .currency(billingCustomer.getCurrency())

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

                String number = billingNumberService.generateSingleNumber(monthName, year);
                billingCore.setBillingNumber(number);
                billingCoreRepository.save(billingCore);
                billingNumberService.saveSingleNumber(number);
                totalDataSuccess++;
            }
            log.info("Finished calculate Billing Core type 1 with month year: {}", request.getMonthYear());
            return "Successfully calculated Billing Core type 1 with total: " + totalDataSuccess;
        } catch (Exception e) {
            log.error("Error when calculate Billing Core type 1: {}", e.getMessage(), e);
            throw new CalculateBillingException("Error when calculate Billing Core type 1", e);
        }
    }

    private static int calculateTransactionHandlingValueFrequency(String aid, List<SkTransaction> skTransactionList) {
        int totalTransactionHandling = skTransactionList.size();
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

}
