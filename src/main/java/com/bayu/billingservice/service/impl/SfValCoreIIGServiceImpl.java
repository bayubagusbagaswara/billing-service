package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.iig.CreateSfValCoreIIGRequest;
import com.bayu.billingservice.model.SfValCoreIIG;
import com.bayu.billingservice.repository.SfValCoreIIGRepository;
import com.bayu.billingservice.service.SfValCoreIIGService;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SfValCoreIIGServiceImpl implements SfValCoreIIGService {

    private final SfValCoreIIGRepository sfValCoreIIGRepository;

    @Override
    public String create(CreateSfValCoreIIGRequest request) {
        try {
            // Input validation
            String customerName = request.getCustomerName();
            String customerCode = request.getCustomerCode();

            BigDecimal totalHolding = BigDecimal.ZERO;
            if (request.getTotalHolding() != null && !request.getTotalHolding().isEmpty()) {
                totalHolding = new BigDecimal(request.getTotalHolding());
            }

            Integer priceTRUB = 0;
            if (request.getPriceTRUB() != null && !request.getPriceTRUB().isEmpty()) {
                priceTRUB = Integer.parseInt(request.getPriceTRUB());
            }

            BigDecimal customerFee = BigDecimal.ZERO;
            if (request.getCustomerFee() != null && !request.getCustomerFee().isEmpty()) {
                customerFee = new BigDecimal(request.getCustomerFee());
            }

            BigDecimal resultCustomerFee = customerFee.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);

            BigDecimal totalMarketValue = calculateTotalMarketValue(totalHolding, priceTRUB);
            BigDecimal safekeepingFee = calculateSafekeepingFee(totalMarketValue, resultCustomerFee);

            // Use a constant for the loop limit
            final int DAYS_IN_MONTH = 31;

            List<SfValCoreIIG> sfValCoreIIGList = new ArrayList<>(DAYS_IN_MONTH);
            for (int i = 1; i <= DAYS_IN_MONTH; i++) {
                SfValCoreIIG sfValCoreIIG = SfValCoreIIG.builder()
                        .customerCodeGroup("IIG")
                        .customerCode(customerCode)
                        .customerName(customerName)
                        .date(i)
                        .totalHolding(totalHolding)
                        .priceTRUB(priceTRUB)
                        .totalMarketValue(totalMarketValue)
                        .safekeepingFee(safekeepingFee)
                        .build();
                sfValCoreIIGList.add(sfValCoreIIG);
            }

            // Use try-with-resources if applicable
            sfValCoreIIGRepository.saveAll(sfValCoreIIGList);

            return "Successfully save all Sf Val Core IIG with customer code: " + customerCode;
        } catch (NumberFormatException | ArithmeticException | DataAccessException e) {
            // Handle exceptions
            // Log the exception and return an appropriate response or rethrow the exception as needed
            e.printStackTrace();
            return "Error processing the request. Please check the input values.";
        }
    }

    @Override
    public List<SfValCoreIIG> getAll() {
        return sfValCoreIIGRepository.findAll();
    }

    @Override
    public List<SfValCoreIIG> getAllByAidAndMonthYear(String aid, String monthYear) {
        List<SfValCoreIIG> sfValCoreIIGList = sfValCoreIIGRepository.findAllByCustomerCodeOrderByDateAsc(aid);
        LocalDate lastDate = ConvertDateUtil.getLatestDateOfMonthYear(monthYear);
        int dayOfMonth = lastDate.getDayOfMonth();

        return sfValCoreIIGList.stream()
                .limit(dayOfMonth)
                .toList();
    }

    private static BigDecimal calculateTotalMarketValue(BigDecimal totalHolding, Integer priceTRUB) {
        return totalHolding.multiply(new BigDecimal(priceTRUB))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateSafekeepingFee(BigDecimal totalMarketValue, BigDecimal customerFee) {
        return totalMarketValue
                .multiply(customerFee)
                .divide(new BigDecimal(365), 2, RoundingMode.HALF_UP);
    }

}
