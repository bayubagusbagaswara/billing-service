package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;
import com.bayu.billingservice.exception.CalculateBillingException;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SkTransaction;
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

import static com.bayu.billingservice.model.enumerator.ApprovalStatus.PENDING;
import static com.bayu.billingservice.model.enumerator.Currency.IDR;
import static com.bayu.billingservice.model.enumerator.FeeParameter.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Core1CalculateServiceImpl implements Core1CalculateService {

    private final KycCustomerService kycCustomerService;
    private final FeeParameterService feeParameterService;
    private final SkTransactionService skTransactionService;
    private final SfValRgDailyService sfValRgDailyService;
    private final BillingNumberService billingNumberService;
    private final BillingCoreRepository billingCoreRepository;

    @Override
    public String calculate(CoreCalculateRequest request) {
        log.info("Start calculate billing core with request : {}", request);
        try {
            String categoryUpperCase = request.getCategory().toUpperCase();
            String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(request.getType());
            String[] monthFormat = ConvertDateUtil.convertToYearMonthFormat(request.getMonthYear());
            String monthName = monthFormat[0];
            int year = Integer.parseInt(monthFormat[1]);

            // Initialization variable
            int transactionHandlingValueFrequency;
            BigDecimal transactionHandlingAmountDue;
            BigDecimal safekeepingValueFrequency;
            BigDecimal safekeepingAmountDue;
            BigDecimal subTotal;
            BigDecimal vatAmountDue;
            BigDecimal totalAmountDue;
            List<BillingCore> billingCoreList = new ArrayList<>();

            // Get data KYC Customer
            List<KycCustomerDTO> kycCustomerDTOList = kycCustomerService.getByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

            // Get data Fee Parameter
            List<String> feeParamList = new ArrayList<>();
            feeParamList.add(TRANSACTION_HANDLING_IDR.getValue());
            feeParamList.add(VAT.getValue());

            Map<String, BigDecimal> feeParamMap = feeParameterService.getValueByNameList(feeParamList);
            BigDecimal transactionHandlingFee = feeParamMap.get(TRANSACTION_HANDLING_IDR.getValue());
            BigDecimal vatFee = feeParamMap.get(VAT.getValue());

            for (KycCustomerDTO kycCustomerDTO : kycCustomerDTOList) {
                String aid = kycCustomerDTO.getAid();
                String billingCategory = kycCustomerDTO.getBillingCategory();
                String billingType = kycCustomerDTO.getBillingType();

                List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(aid, monthName, year);

                List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);

                transactionHandlingValueFrequency = calculateTransactionHandlingValueFrequency(aid, skTransactionList);

                transactionHandlingAmountDue = calculateTransactionHandlingAmountDue(aid, transactionHandlingFee, transactionHandlingValueFrequency);

                safekeepingValueFrequency = calculateSafekeepingValueFrequency(aid, sfValRgDailyList);

                safekeepingAmountDue = calculateSafekeepingAmountDue(aid, sfValRgDailyList);

                subTotal = calculateSubTotalAmountDue(aid, transactionHandlingAmountDue, safekeepingAmountDue);

                vatAmountDue = calculateVatAmountDue(aid, subTotal, vatFee);

                totalAmountDue = calculateTotalAmountDue(aid, subTotal, vatAmountDue);

                Optional<BillingCore> existingBillingCore = billingCoreRepository.findByAidAndBillingCategoryAndBillingTypeAndMonthAndYear(aid, billingCategory, billingType, monthName, year);

                if (existingBillingCore.isPresent()) {
                    // Update existing record
                    BillingCore existBillingCore = existingBillingCore.get();
                    String billingNumber = existBillingCore.getBillingNumber();
                    billingCoreRepository.delete(existBillingCore);
                    billingNumberService.deleteByBillingNumber(billingNumber);
                }

                Instant dateNow = Instant.now();
                BillingCore billingCore = BillingCore.builder()
                        .createdAt(dateNow)
                        .updatedAt(dateNow)
                        .approvalStatus(PENDING.getStatus())
                        .aid(kycCustomerDTO.getAid())
                        .month(monthName)
                        .year(year)
                        .billingPeriod(monthName + " " + year)
                        .billingStatementDate(ConvertDateUtil.convertInstantToString(dateNow))
                        .billingPaymentDueDate(ConvertDateUtil.convertInstantToStringPlus14Days(dateNow))
                        .billingCategory(kycCustomerDTO.getBillingCategory())
                        .billingType(kycCustomerDTO.getBillingType())
                        .billingTemplate(kycCustomerDTO.getBillingTemplate())
                        .investmentManagementName(kycCustomerDTO.getInvestmentManagementName())
                        .investmentManagementAddress(kycCustomerDTO.getInvestmentManagementAddress())
                        .productName(kycCustomerDTO.getProductName())
                        .accountName(kycCustomerDTO.getAccountName())
                        .accountNumber(kycCustomerDTO.getAccountNumber())
                        .accountBank(kycCustomerDTO.getAccountBank())
                        .currency(IDR.getValue())
                        .minimumFee(kycCustomerDTO.getMinimumFee())
                        .transactionHandlingValueFrequency(transactionHandlingValueFrequency)
                        .transactionHandlingFee(transactionHandlingFee)
                        .transactionHandlingAmountDue(transactionHandlingAmountDue)
                        .safekeepingValueFrequency(safekeepingValueFrequency)
                        .safekeepingFee(kycCustomerDTO.getCustomerFee())
                        .safekeepingAmountDue(safekeepingAmountDue)
                        .subTotal(subTotal)
                        .vatFee(vatFee)
                        .vatAmountDue(vatAmountDue)
                        .totalAmountDue(totalAmountDue)
                        .build();

                billingCoreList.add(billingCore);
            }

            int billingCoreListSize = billingCoreList.size();
            List<String> numberList = billingNumberService.generateNumberList(billingCoreListSize, monthName, year);

            for (int i = 0; i < billingCoreListSize; i++) {
                BillingCore billingCore = billingCoreList.get(i);
                String billingNumber = numberList.get(i);
                billingCore.setBillingNumber(billingNumber);
            }

            billingCoreRepository.saveAll(billingCoreList);
            billingNumberService.saveAll(numberList);

            log.info("Finished calculate Billing Core type 1 with month '{}' and year '{}'", monthName, year);
            return "Successfully calculated Billing Core type 1 with a total : " + billingCoreListSize;
        } catch (Exception e) {
            log.error("Error when calculate Billing Core type 1 : " + e.getMessage(), e);
            throw new CalculateBillingException("Error when calculate Billing Core type 1 : " + e.getMessage());
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
