package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.BillingNumberDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
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
    public String saveAll(List<String> numberList) {
        List<BillingNumber> billingNumberList = new ArrayList<>();

        for (String billingNumber : numberList) {
            log.info("[Billing Number]: {}", billingNumber);
            BillingNumber billingNumberEntity = parseBillingNumber(billingNumber);

            if (billingNumberEntity != null) {
                log.info("[Parsed Billing Number]: {}", billingNumberEntity);
                billingNumberList.add(billingNumberEntity);
            } else {
                log.warn("Invalid billing number format: {}", billingNumber);
                // Handle invalid billing number format if needed
            }
        }

        billingNumberRepository.saveAll(billingNumberList);
        return "Successfully saved all Billing Numbers";
    }

    @Override
    public String saveSingleNumber(String number) {
        log.info("Save single billing number: {}", number);
        BillingNumber billingNumber = parseBillingNumber(number);
        if (billingNumber == null) {
            throw new GeneralException("Failed parse billing number: " + number);
        }
        log.info("Parse single billing number: {}", billingNumber);
        BillingNumber save = billingNumberRepository.save(billingNumber);
        return "Successfully save single billing number with id: " + save.getId();
    }

    @Override
    public String generateSingleNumber(String month, int year) {
        return "";
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

    @Override
    public List<String> generateNumberList(int billingSize, String monthName, int year) {
        Integer maxSequenceNumberByMonthAndYear = getMaxSequenceNumberByMonthAndYear(monthName, year);

        List<String> billingNumberList = new ArrayList<>();

        for (int i = 0; i < billingSize; i++) {

            // int currentBillingNumber = (maxSequenceNumberByMonthAndYear != null ? maxSequenceNumberByMonthAndYear : 0) + i + 1;
            int currentBillingNumber = (maxSequenceNumberByMonthAndYear != null ? maxSequenceNumberByMonthAndYear + 1 : 1) + i;

            Month month = Month.valueOf(monthName.toUpperCase());
            String monthFormat = String.format("%02d", month.getValue());
            int lastTwoDigits = year % 100;

            billingNumberList.add(String.format("C%03d/SS-BS/%s%d", currentBillingNumber, monthFormat, lastTwoDigits));
        }

        for (String s : billingNumberList) {
            log.info("Billing Number List : {}", s);
        }
        return billingNumberList;
    }

    @Override
    public void deleteByBillingNumber(String number) {
        BillingNumber billingNumber = billingNumberRepository.findByNumber(number)
                .orElseThrow(() -> new DataNotFoundException("Billing Number with number '" + number + "' is not found"));
        billingNumberRepository.delete(billingNumber);
        // return "Successfully delete billing number with number : " + number;
    }

    public static BillingNumber parseBillingNumber(String billingNumber) {
        // Define a regex pattern to extract the sequence number, month, and year
        Pattern pattern = Pattern.compile("C(\\d+)/SS-BS/(\\d{2})(\\d{2})");
        Matcher matcher = pattern.matcher(billingNumber);

        if (matcher.matches()) {
            int sequenceNumber = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int lastTwoDigits = Integer.parseInt(matcher.group(3));
            String monthFullName = getMonthFullName(month);

            int firstTwoDigits = 20;
            int fullYear = firstTwoDigits * 100 + lastTwoDigits;

            return BillingNumber.builder()
                    .createdAt(new Date())
                    .sequenceNumber(sequenceNumber)
                    .month(monthFullName)
                    .year(fullYear)
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
