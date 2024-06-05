package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.billing.BillingCalculationErrorMessageDTO;
import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.billing.BillingContextDate;
import com.bayu.billingservice.dto.core.CoreCalculateRequest;
import com.bayu.billingservice.dto.core.CoreTemplate2;
import com.bayu.billingservice.dto.core.CoreType7Parameter;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.*;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoreType7ServiceImpl implements CoreType7Service {

    private static final String MUFG = "12MUFG";

    private final BillingCoreRepository billingCoreRepository;
    private final CustomerService customerService;
    private final InvestmentManagementService investmentManagementService;
    private final FeeParameterService feeParameterService;
    private final FeeScheduleService feeScheduleService;
    private final SkTransactionService skTransactionService;
    private final SfValRgMonthlyService sfValRgMonthlyService;
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

        /* get previous month and year */
        String[][] previousMonthsAndYears = ConvertDateUtil.getPreviousMonthsAndYears(contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

        List<List<SkTransaction>> skTransactionsList = new ArrayList<>();
        List<List<SfValRgMonthly>> sfValRgMonthliesList = new ArrayList<>();
        List<BigDecimal> kseiAmountFeeList = new ArrayList<>();

        /* get data fee parameters */
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameter.VAT.getValue());
        BigDecimal kseiTransactionFee = feeParameterService.getValueByName(FeeParameter.KSEI.getValue());

        /* get all customer Core Type 3 */
        List<Customer> customerList = customerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

        String customerCode = null;

        try {
            for (Customer customer : customerList) {
                customerCode = customer.getCustomerCode();
                String kseiSafeCode = customer.getKseiSafeCode();

                for (String[] previousMonthsAndYear : previousMonthsAndYears) {
                    String monthInput = previousMonthsAndYear[0];
                    Integer yearInput = Integer.parseInt(previousMonthsAndYear[1]);

                    // Get SfValRgMonthly Month [i] and Year [i]
                    List<SfValRgMonthly> sfValRgMonthlyList = sfValRgMonthlyService.getAllByCustomerCodeAndMonthAndYear(customerCode, monthInput, yearInput);
                    sfValRgMonthliesList.add(sfValRgMonthlyList);

                    // Get KSEI Safe Fee Month [i] and Year [i]
                    KseiSafekeepingFee kseiSafekeepingFee = kseiSafekeepingFeeService.getByKseiSafeCodeAndMonthAndYear(kseiSafeCode, monthInput, yearInput);
                    kseiAmountFeeList.add(kseiSafekeepingFee.getAmountFee());

                    // Get SK Transaction Month [i] and Year [i]
                    List<SkTransaction> skTransactionList = skTransactionService.getAllByAidAndMonthAndYear(customerCode, monthInput, yearInput);
                    skTransactionsList.add(skTransactionList);
                }
            }

            // Get data dari billing customer list, filter by customerCode is 12MUFG
            Customer customer12MUFG = customerList.stream()
                    .filter(data -> data.getCustomerCode().equalsIgnoreCase(MUFG))
                    .findFirst()
                    .orElseThrow(() -> new DataNotFoundException("Customer not found with customer code: " + MUFG));

            // check existing billing by customer code
            Optional<BillingCore> existingBillingCore = billingCoreRepository.findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(
                    customer12MUFG.getCustomerCode(), customer12MUFG.getBillingCategory(), customer12MUFG.getBillingType(), contextDate.getMonthNameMinus1(), contextDate.getYearMinus1());

            if (existingBillingCore.isEmpty() || Boolean.TRUE.equals(!existingBillingCore.get().getPaid())) {
                existingBillingCore.ifPresent(this::deleteExistingBillingCore);



            } else {
                addErrorMessage(errorMessageList, customer12MUFG.getCustomerCode(), "Billing already paid for period " + contextDate.getMonthNameMinus1() + " " + contextDate.getYearMinus1());
                totalDataFailed++;
            }
        } catch (Exception e) {
            handleGeneralError(customerCode, e, errorMessageList);
            totalDataFailed++;
        }
        log.info("Total successfully calculations: {}, total failed calculations: {}", totalDataSuccess, totalDataFailed);
        return new BillingCalculationResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private CoreTemplate2 calculationResult(CoreType7Parameter param) {
        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(param.getSfValRgMonthliesList());
        BigDecimal subTotal = calculateSubTotal(safekeepingAmountDue);
        BigDecimal vatAmountDue = calculateVATAmountDue(safekeepingAmountDue, param.getVatFee());
        Integer kseiTransactionValueFrequency = calculateKseiTransactionValueFrequency(param.getSkTransactionsList());
        BigDecimal kseiTransactionAmountDue = calculateKseiSafekeepingAmountDue(kseiTransactionValueFrequency, param.getKseiTransactionFee());
        BigDecimal kseiSafeFeeAmountDue = calculateKseiSafeFeeAmountDue(param.getVatFee(), param.getKseiAmountFeeList());
        BigDecimal totalAmountDue = calculateTotalAmountDue(subTotal, vatAmountDue, kseiSafeFeeAmountDue, kseiTransactionAmountDue);

        return CoreTemplate2.builder()
                .safekeepingValueFrequency(BigDecimal.ZERO)
                .safekeepingFee(param.getCustomerSafekeepingFee())
                .safekeepingAmountDue(safekeepingAmountDue)
                .vatFee(param.getVatFee())
                .vatAmountDue(vatAmountDue)
                .kseiTransactionValueFrequency(kseiTransactionValueFrequency)
                .kseiTransactionFee(param.getKseiTransactionFee())
                .kseiTransactionAmountDue(kseiTransactionAmountDue)
                .kseiSafekeepingAmountDue(kseiSafeFeeAmountDue)
                .totalAmountDue(totalAmountDue)
                .build();
    }

    private BigDecimal calculateSafekeepingAmountDue(List<List<SfValRgMonthly>> sfValRgMonthliesList) {
        List<BigDecimal> safekeepingValueFrequencyList = new ArrayList<>();
        List<SfValRgMonthly> rawList = new ArrayList<>();

        for (List<SfValRgMonthly> sfValRgMonthlyList : sfValRgMonthliesList) {
            rawList.addAll(sfValRgMonthlyList);
        }

        List<SfValRgMonthly> sorted = rawList.stream()
                .sorted(Comparator.comparing(SfValRgMonthly::getDate).reversed())
                .toList();

        // Grouping by month and year
        Map<String, List<SfValRgMonthly>> groupedByMonthAndYear = new HashMap<>();
        for (SfValRgMonthly sf : sorted) {
            String key = sf.getDate().getYear() + "-" + sf.getDate().getMonthValue(); // Forming key as "YYYY-MM"
            groupedByMonthAndYear.computeIfAbsent(key, k -> new ArrayList<>()).add(sf);
        }

        // Adding the grouped lists to the 'lists' list
        List<List<SfValRgMonthly>> lists = new ArrayList<>(groupedByMonthAndYear.values());

        for (List<SfValRgMonthly> list : lists) {
            List<SfValRgMonthly> latestEntries = list.stream()
                    .filter(entry -> entry.getDate().equals(list.stream()
                            .map(SfValRgMonthly::getDate)
                            .max(Comparator.naturalOrder())
                            .orElse(null)))
                    .toList();

            BigDecimal safekeepingValueFrequency = latestEntries.stream()
                    .map(SfValRgMonthly::getMarketValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP);

            BigDecimal feeScheduleFeeValue = feeScheduleService.checkFeeScheduleAndGetFeeValue(safekeepingValueFrequency);
            BigDecimal result = feeScheduleFeeValue.divide(new BigDecimal(12), 0, RoundingMode.HALF_UP);
            safekeepingValueFrequencyList.add(result);
        }

        BigDecimal safekeepingValueFrequency3Months = safekeepingValueFrequencyList.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 7] Safekeeping value frequency 3 months (-PPn): {}", safekeepingValueFrequency3Months);
        return safekeepingValueFrequency3Months;
    }

    private static Integer calculateKseiTransactionValueFrequency(List<List<SkTransaction>> skTransactionsList) {
        List<Integer> kseiTransactionValueFrequency = new ArrayList<>();

        for (List<SkTransaction> skTransactions : skTransactionsList) {
            int size = skTransactions.size();
            kseiTransactionValueFrequency.add(size);
        }

        int sum = kseiTransactionValueFrequency.stream()
                .mapToInt(Integer::intValue)
                .sum();

        log.info("[Core Type 7] Total KSEI transaction frequency: {}", sum);
        return sum;
    }

    private static BigDecimal calculateKseiSafekeepingAmountDue(Integer kseiTransactionValueFrequency, BigDecimal kseiTransactionFee) {
        BigDecimal kseiSafekeepingAmountDue = new BigDecimal(kseiTransactionValueFrequency)
                .multiply(kseiTransactionFee)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 7] KSEI safekeeping amount due: {}", kseiSafekeepingAmountDue);
        return kseiSafekeepingAmountDue;
    }

    private static BigDecimal calculateVATAmountDue(BigDecimal safekeepingAmountDue, BigDecimal vatFee) {
        BigDecimal vatAmountDue = safekeepingAmountDue
                .multiply(vatFee)
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("[Core Type 7] VAT amount due: {}", vatAmountDue);
        return vatAmountDue;
    }

    private static BigDecimal calculateKseiSafeFeeAmountDue(BigDecimal vatFee, List<BigDecimal> kseiAmountFeeList) {
        BigDecimal kseiSafeFeeAmountDue = kseiAmountFeeList.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        BigDecimal result = kseiSafeFeeAmountDue.multiply(vatFee)
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP);

        BigDecimal add = kseiSafeFeeAmountDue.add(result);

        log.info("[Core Type 7] Ksei Safe fee amount due: {}", add);
        return add;
    }

    private static BigDecimal calculateSubTotal(BigDecimal safekeepingAmountDue) {
        log.info("[Core Type 7] Sub total: {}", safekeepingAmountDue);
        return safekeepingAmountDue;
    }

    private static BigDecimal calculateTotalAmountDue(BigDecimal subTotal, BigDecimal vatAmountDue, BigDecimal kseiSafeFeeAmountDue, BigDecimal kseiTransactionAmountDue) {
        BigDecimal totalAmountDue = subTotal
                .add(vatAmountDue)
                .add(kseiSafeFeeAmountDue)
                .add(kseiTransactionAmountDue).setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 7] Total amount due: {}", totalAmountDue);
        return totalAmountDue;
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
        errorMessageList.add(new BillingCalculationErrorMessageDTO(customerCode.isEmpty() ? "unknown" : customerCode, stringList));
    }

}
