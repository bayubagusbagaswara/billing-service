package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.repository.BillingCoreRepository;
import com.bayu.billingservice.service.*;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreType4ServiceImpl implements CoreType4Service {

    private final FeeParameterService feeParameterService;
    private final SkTransactionService skTransactionService;
    private final SfValRgDailyService sfValRgDailyService;
    private final KseiSafekeepingFeeService kseiSafekeepingFeeService;
    private final BillingNumberService billingNumberService;
    private final BillingCoreRepository billingCoreRepository;
    private final ConvertDateUtil convertDateUtil;

    @Override
    public String calculate(CoreCalculateRequest request) {
//        try {
//            String categoryUpperCase = request.getCategory().toUpperCase();
//            String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());
//            String[] monthFormat = ConvertDateUtil.convertToYearMonthFormat(request.getMonthYear());
//            String monthName = monthFormat[0];
//            int year = Integer.parseInt(monthFormat[1]);
//
//            List<BillingCore> billingCoreList = new ArrayList<>();
//
//            List<CustomerDTO> customerDTOList = customerService.getByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);
//
//            // Get data Fee Parameter
//            List<String> feeParamList = new ArrayList<>();
//            feeParamList.add(KSEI.getValue());
//            feeParamList.add(VAT.getValue());
//
//            Map<String, BigDecimal> feeParamMap = feeParameterService.getValueByNameList(feeParamList);
//            BigDecimal kseiTransactionFee = feeParamMap.get(KSEI.getValue());
//            BigDecimal vatFee = feeParamMap.get(VAT.getValue());
//
//            for (CustomerDTO customerDTO : customerDTOList) {
//                String aid = customerDTO.getCustomerCode();
//                String kseiSafeCode = customerDTO.getKseiSafeCode();
//                BigDecimal customerSafekeepingFee = customerDTO.getCustomerSafekeepingFee();
//                String billingCategory = customerDTO.getBillingCategory();
//                String billingTemplate = customerDTO.getBillingTemplate();
//                String billingTemplateFormat = billingCategory + "_" + billingTemplate;
//
//                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(aid, monthName, year);
//
//                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);
//
//                BigDecimal kseiSafeFeeAmount = kseiSafekeepingFeeService.calculateAmountFeeByCustomerCodeAndMonthAndYear(kseiSafeCode, monthName, year);
//
//                BillingCore billingCore;
//                Instant dateNow = Instant.now();
//                if (BillingTemplate.CORE_TEMPLATE_5.getValue().equalsIgnoreCase(billingTemplateFormat)) {
//                    // calculate EB
//                    billingCore = calculateEB(aid, billingTemplate, kseiSafeFeeAmount, kseiTransactionFee, skTransactionList);
//                } else {
//                    // calculate ITAMA
//                    billingCore = calculateITAMA(aid, billingTemplate, customerSafekeepingFee, vatFee, sfValRgDailyList);
//                }
//
//                // TODO: Set fields common to BillingCore, common to Core 4 (EB and ITAMA)
//                billingCore.setApprovalStatus(ApprovalStatus.PENDING);
//                billingCore.setMonth(monthName);
//                billingCore.setYear(year);
//                billingCore.setBillingPeriod(monthName + " " + year);
//                billingCore.setBillingStatementDate(convertDateUtil.convertInstantToString(dateNow));
//                billingCore.setBillingPaymentDueDate(convertDateUtil.convertInstantToStringPlus14Days(dateNow));
//                billingCore.setBillingCategory(customerDTO.getBillingCategory());
//                billingCore.setBillingType(customerDTO.getBillingType());
//                billingCore.setBillingTemplate(customerDTO.getBillingTemplate());
//                billingCore.setInvestmentManagementName(customerDTO.getInvestmentManagementName());
////                billingCore.setInvestmentManagementAddress(billingCustomerDTO.getInvestmentManagementAddress());
//                billingCore.setAccountName(customerDTO.getAccountName());
//                billingCore.setAccountNumber(customerDTO.getAccountNumber());
//                billingCore.setCostCenter(customerDTO.getCostCenter()); // EB
//                billingCore.setAccountBank(customerDTO.getAccountBank()); // ITAMA
//
//                billingCoreList.add(billingCore);
//            }
//
//            int billingCoreListSize = billingCoreList.size();
//            List<String> numberList = billingNumberService.generateNumberList(billingCoreListSize, monthName, year);
//
//            for (int i = 0; i < billingCoreListSize; i++) {
//                BillingCore billingCore = billingCoreList.get(i);
//                String billingNumber = numberList.get(i);
//                billingCore.setBillingNumber(billingNumber);
//            }
//
//            List<BillingCore> billingCoreSaved = billingCoreRepository.saveAll(billingCoreList);
//
//            log.info("Finished calculate Billing Core type 4 with period '{}'", request.getMonthYear());
//            return "Successfully calculated Billing Core type 4 with a total : " + billingCoreSaved.size();
//        } catch (Exception e) {
//            log.error("Error when calculate Billing Core type 4 : " + e.getMessage(), e);
//            throw new CalculateBillingException("Error when calculate Billing Core type 4 : " + e.getMessage());
//        }
        return null;
    }

    // TODO: [ITAMA] Calculate Safekeeping Value Frequency [DONE]
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

        log.info("[Core Type 4 ITAMA] Safekeeping value frequency Aid '{}' is '{}'", aid, safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    // TODO: [ITAMA] Calculate Safekeeping Amount Due [DONE]
    private static BigDecimal calculateSafekeepingAmountDue(String aid, List<SfValRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingAmountDue = sfValRgDailyList.stream()
                .map(SfValRgDaily::getEstimationSafekeepingFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 4 ITAMA] Safekeeping amount due Aid '{}' is '{}'", aid, safekeepingAmountDue);
        return safekeepingAmountDue;
    }

    // TODO: [ITAMA] Calculate VAT Amount Due [DONE]
    private static BigDecimal calculateVatAmountDue(String aid, BigDecimal subTotalAmountDue, BigDecimal vatFee) {
        BigDecimal vatAmountDue = subTotalAmountDue.multiply(vatFee).setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 4 ITAMA] VAT amount due Aid '{}' is '{}'", aid, vatAmountDue);
        return vatAmountDue;
    }

    // TODO: [ITAMA] Calculate Total Amount Due [DONE]
    private static BigDecimal calculateTotalAmountDueITAMA(String aid, BigDecimal safekeepingAmountDue, BigDecimal vatAmountDue) {
        BigDecimal totalAmountDueITAMA = safekeepingAmountDue.add(vatAmountDue);
        log.info("[Core Type 4] Total amount due ITAMA Aid '{}' is '{}'", aid, vatAmountDue);
        return totalAmountDueITAMA;
    }

    // TODO: [ITAMA] Create Object Itama to BilingCore model [DONE]
    private static BillingCore calculateITAMA(String aid, String billingTemplate, BigDecimal customerSafekeepingFee, BigDecimal vatFee, List<SfValRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingValueFrequency = calculateSafekeepingValueFrequency(aid, sfValRgDailyList);
        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(aid, sfValRgDailyList);
        BigDecimal vatAmountDue = calculateVatAmountDue(aid, safekeepingAmountDue, vatFee);
        BigDecimal totalAmountDueITAMA = calculateTotalAmountDueITAMA(aid, safekeepingAmountDue, vatAmountDue);

        return BillingCore.builder()
                .customerCode(aid)
                .billingTemplate(billingTemplate)
                .safekeepingValueFrequency(safekeepingValueFrequency)
                .safekeepingFee(customerSafekeepingFee)
                .safekeepingAmountDue(safekeepingAmountDue)
                .vatFee(vatFee)
                .vatAmountDue(vatAmountDue)
                .totalAmountDue(totalAmountDueITAMA)
                .build();
    }

    // TODO: [EB] Calculate Transaction Value Frequency [DONE]
    private static int calculateTransactionHandlingValueFrequency(String aid, List<SkTransaction> skTransactionList) {
        int totalTransactionHandling = skTransactionList.size();
        log.info("[Core Type 4 EB] Total transaction handling Aid '{}' is '{}'", aid, totalTransactionHandling);
        return totalTransactionHandling;
    }

    // TODO: [EB] Calculate KSEI Transaction Amount Due [DONE]
    private static BigDecimal calculateKSEITransactionAmountDue(String aid, BigDecimal kseiTransactionFee, Integer transactionValueFrequency) {
        BigDecimal kseiTransactionAmountDue = kseiTransactionFee.multiply(new BigDecimal(transactionValueFrequency))
                .setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 4 EB] KSEI transaction amount due Aid '{}' is '{}'", aid, kseiTransactionAmountDue);
        return kseiTransactionAmountDue;
    }

    // TODO: [EB] Calculate Total Amount Due
    private static BigDecimal calculateTotalAmountDueEB(String aid, BigDecimal kseiSafekeepingAmountDue, BigDecimal kseiTransactionAmountDue) {
        BigDecimal totalAmountDueEB = kseiSafekeepingAmountDue.add(kseiTransactionAmountDue);
        log.info("[Core Type 4 EB] Total amount due Aid '{}' is '{}'", aid, totalAmountDueEB);
        return totalAmountDueEB;
    }

    // TODO: [EB] Create Object EB to BillingCore model [DONE]
    private static BillingCore calculateEB(String aid, String billingTemplate, BigDecimal kseiSafeFeeAmount, BigDecimal kseiTransactionFee, List<SkTransaction> skTransactionList) {
        int kseiTransactionValueFrequency = calculateTransactionHandlingValueFrequency(aid, skTransactionList);
        BigDecimal kseiTransactionAmountDue = calculateKSEITransactionAmountDue(aid, kseiTransactionFee, kseiTransactionValueFrequency);
        BigDecimal totalAmountDueEB = calculateTotalAmountDueEB(aid, kseiSafeFeeAmount, kseiTransactionAmountDue);

        return BillingCore.builder()
                .customerCode(aid)
                .billingTemplate(billingTemplate)
                .kseiSafekeepingAmountDue(kseiSafeFeeAmount)
                .kseiTransactionValueFrequency(kseiTransactionValueFrequency)
                .kseiTransactionFee(kseiTransactionFee)
                .kseiTransactionAmountDue(kseiTransactionAmountDue)
                .totalAmountDue(totalAmountDueEB)
                .build();
    }

}
