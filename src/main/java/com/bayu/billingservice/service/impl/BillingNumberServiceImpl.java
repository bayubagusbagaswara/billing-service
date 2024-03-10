package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.BillingNumberDTO;
import com.bayu.billingservice.model.BillingNumber;
import com.bayu.billingservice.repository.BillingNumberRepository;
import com.bayu.billingservice.service.BillingNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingNumberServiceImpl implements BillingNumberService {

    private final BillingNumberRepository billingNumberRepository;

    @Override
    public String saveToAll(List<String> numberList) {
        List<BillingNumber> billingNumberList = new ArrayList<>();

        for (String billingNumber : numberList) {
            BillingNumber billingAccountNumber = parseBillingNumber(billingNumber);
            billingNumberList.add(billingAccountNumber);
        }

        billingNumberRepository.saveAll(billingNumberList);

        return "Successfully saved all Billing Numbers";
    }

    @Override
    public Integer getMaxSequenceNumberByMonthAndYear(String month, int year) {
        return billingNumberRepository.getMaxSequenceNumberByMonthAndYear(month, year);
    }

    @Override
    public List<BillingNumberDTO> getAll() {
        List<BillingNumber> billingNumberList = billingNumberRepository.findAll();
        return mapToDTOList(billingNumberList);
    }

    public static BillingNumber parseBillingNumber(String billingNumber) {
        // Define a regex pattern to extract the sequence number, month, and year
        Pattern pattern = Pattern.compile("C(\\d+)/SS-BS/(\\d{2})(\\d{2})");
        Matcher matcher = pattern.matcher(billingNumber);

        if (matcher.matches()) {
            int sequenceNumber = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int year = Integer.parseInt(matcher.group(3));

            return BillingNumber.builder()
                    .createdAt(new Date())
                    .sequenceNumber(sequenceNumber)
                    .month(getMonthFullName(month))
                    .year(year)
                    .number(billingNumber)
                    .build();
        }

        return null; // Return null for invalid format
    }

    private static String getMonthFullName(int monthValue) {
        if (monthValue < 1 || monthValue > 12) {
            throw new IllegalArgumentException("Invalid month value");
        }
        Month month = Month.of(monthValue);

        return month.getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    private static BillingNumberDTO mapToDTO(BillingNumber billingNumber) {
        return BillingNumberDTO.builder()
                .id(String.valueOf(billingNumber.getId()))
                .sequenceNumber(String.valueOf(billingNumber.getSequenceNumber()))
                .month(billingNumber.getMonth())
                .year(String.valueOf(billingNumber.getYear()))
                .createdDate(String.valueOf(billingNumber.getCreatedAt()))
                .number(billingNumber.getNumber())
                .build();
    }

    private static List<BillingNumberDTO> mapToDTOList(List<BillingNumber> billingNumberList) {
        return billingNumberList.stream()
                .map(BillingNumberServiceImpl::mapToDTO)
                .toList();
    }
}