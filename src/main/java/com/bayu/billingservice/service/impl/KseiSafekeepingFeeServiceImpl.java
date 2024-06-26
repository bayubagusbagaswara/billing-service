package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.kseisafe.CreateKseiSafeRequest;
import com.bayu.billingservice.exception.*;
import com.bayu.billingservice.model.KseiSafekeepingFee;
import com.bayu.billingservice.model.enumerator.FeeParameter;
import com.bayu.billingservice.repository.KseiSafekeepingFeeRepository;
import com.bayu.billingservice.service.FeeParameterService;
import com.bayu.billingservice.service.KseiSafekeepingFeeService;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KseiSafekeepingFeeServiceImpl implements KseiSafekeepingFeeService {

    private static final String BASE_FILE_NAME = "Ksei_Safe_";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private final KseiSafekeepingFeeRepository kseiSafekeepingFeeRepository;
    private final FeeParameterService feeParameterService;
    private final ConvertDateUtil convertDateUtil;

    @Override
    public List<KseiSafekeepingFee> create(List<CreateKseiSafeRequest> requestList) {

        List<KseiSafekeepingFee> kseiSafekeepingFeeList = new ArrayList<>();

        for (CreateKseiSafeRequest request : requestList) {
            LocalDate localDate = LocalDate.parse(request.getCreatedDate());
            int year = localDate.getYear();
            String monthName = localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            KseiSafekeepingFee kseiSafekeepingFee = KseiSafekeepingFee.builder()
                    .createdDate(localDate)
                    .month(monthName)
                    .year(year)
                    .feeDescription(request.getFeeDescription())
                    .kseiSafeCode(request.getCustomerCode())
                    .amountFee(new BigDecimal(request.getAmountFee()))
                    .build();

            kseiSafekeepingFeeList.add(kseiSafekeepingFee);
        }

        return kseiSafekeepingFeeRepository.saveAll(kseiSafekeepingFeeList);
    }

    @Override
    public String readAndInsertToDB(String filePath, String monthYear) {
        log.info("File Path: {}, and Month Year: {}", filePath, monthYear);

        try {
            Map<String, String> monthMinus1 = convertDateUtil.getMonthMinus1();
            String monthName = monthMinus1.get("monthName");
            String monthValue = monthMinus1.get("monthValue");
            int year = Integer.parseInt(monthMinus1.get("year"));

            String fileName = BASE_FILE_NAME + year + monthValue + ".xlsx";
            String filePathNew = filePath + fileName;

            if (StringUtils.isBlank(filePathNew)) {
                throw new InvalidInputException("File path cannot be null or empty");
            }

            // Check if the file exists
            File file = new File(filePathNew);
            if (!file.exists()) {
                log.error("File not found: {}", filePathNew);
                throw new DataNotFoundException("KSEI Safe Fee file not found with path: " + filePathNew);
            }

            // Find and delete existing data for the specified month and year
            kseiSafekeepingFeeRepository.deleteByMonthAndYear(monthName, year);

            List<KseiSafekeepingFee> kseiSafekeepingFeeList = readDataFromFile(file);
            kseiSafekeepingFeeRepository.saveAll(kseiSafekeepingFeeList);
            return "Excel data processed and saved successfully";
        } catch (DataNotFoundException e) {
            log.error("Data not found: {}", e.getMessage());
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage(), e);
            throw new GeneralException(e.getMessage());
        }
    }

    private List<KseiSafekeepingFee> readDataFromFile(File file) {
        List<KseiSafekeepingFee> kseiSafekeepingFeeList = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))) {
            processSheets(workbook, kseiSafekeepingFeeList);
        } catch (IOException e) {
            log.error("Error reading the Excel file: {}", e.getMessage());
            throw new ReadExcelException("Failed to process Excel file. Error reading the Excel file: " + e.getMessage());
        }
        return kseiSafekeepingFeeList;
    }

    @Override
    public List<KseiSafekeepingFee> getAll() {
        return kseiSafekeepingFeeRepository.findAll();
    }

    @Override
    public List<KseiSafekeepingFee> getByCustomerCode(String customerCode) {
        return kseiSafekeepingFeeRepository.findByKseiSafeCodeContainingIgnoreCase(customerCode);
    }

    @Override
    public BigDecimal calculateAmountFeeByKseiSafeCodeAndMonthAndYear(String kseiSafeCode, String monthName, int year) {
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameter.VAT.getValue());

        KseiSafekeepingFee kseiSafekeepingFee = kseiSafekeepingFeeRepository.findByKseiSafeCodeAndMonthAndYear(kseiSafeCode, monthName, year)
                .orElseThrow(() -> new DataNotFoundException("KSEI Safekeeping Fee not found with ksei safe code " + kseiSafeCode + ", month " + monthName + ", and year " + year));

        BigDecimal amountFee = kseiSafekeepingFee.getAmountFee();

        BigDecimal valueAfterVAT = amountFee.multiply(vatFee)
                .divide(new BigDecimal(100), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);

        return amountFee.add(valueAfterVAT).setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateAmountFeeForLast3Months(String customerCode, String month, int year) {
        String monthYear = month + " " + year;
        LocalDate firstDateOfMonthYear = convertDateUtil.getFirstDateOfMonthYear(monthYear);

        LocalDate endDate = firstDateOfMonthYear.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate startDate = endDate.minusMonths(2).with(TemporalAdjusters.lastDayOfMonth()); // 3 bulan kebelakang
        log.info("Start Date : {}, and End Date : {}", startDate, endDate);

        List<KseiSafekeepingFee> filteredData = kseiSafekeepingFeeRepository.findByCustomerCodeAndDateBetweenNative(customerCode, startDate, endDate);

        for (KseiSafekeepingFee filteredDatum : filteredData) {
            log.info("Amount : {}", filteredDatum.getAmountFee());
        }

        BigDecimal totalAmount = filteredData.stream()
                .map(entity -> Objects.requireNonNullElse(entity.getAmountFee(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Total Amount : {}", totalAmount);

        return totalAmount;
    }

    @Override
    public String deleteAll() {
        try {
            kseiSafekeepingFeeRepository.deleteAll();
            return "Successfully deleted all KSEI Safekeeping Fee";
        } catch (Exception e) {
            log.error("Error when delete all KSEI Safekeeping Fee : " + e.getMessage());
            throw new ConnectionDatabaseException("Error when delete all KSEI Safekeeping Fee");
        }
    }

    @Override
    public KseiSafekeepingFee getByKseiSafeCodeAndMonthAndYear(String kseiSafeCode, String month, Integer year) {
        return kseiSafekeepingFeeRepository.findByKseiSafeCodeAndMonthAndYear(kseiSafeCode, month, year)
                .orElseThrow(() -> new DataNotFoundException("Ksei Safe Fee with ksei safe code '" + kseiSafeCode + "' " +
                        "and month '" + month + "' " +
                        "and year '" + year + "'"));
    }

    private static void processSheets(Workbook workbook, List<KseiSafekeepingFee> kseiSafekeepingFeeList) {
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            processRows(sheet, kseiSafekeepingFeeList);
        }
    }

    private static void processRows(Sheet sheet, List<KseiSafekeepingFee> kseiSafekeepingFeeList) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        // Skip the first row (header)
        if (rowIterator.hasNext()) {
            rowIterator.next(); // move to the next row
        }

        while (rowIterator.hasNext()) {
            try {
                Row row = rowIterator.next();
                KseiSafekeepingFee kseiSafekeepingFee = createEntityFromRow(row);
                kseiSafekeepingFeeList.add(kseiSafekeepingFee);
            } catch (Exception e) {
                log.error("Error processing a row: {}", e.getMessage(), e);
                // You may choose to continue processing other rows or break the loop
                throw new GeneralException("Failed to process Excel file: " + e.getMessage());
            }
        }
    }

    private static KseiSafekeepingFee createEntityFromRow(Row row) {
        KseiSafekeepingFee kseiSafekeepingFee = new KseiSafekeepingFee();
        Cell cell3 = row.getCell(2);
        kseiSafekeepingFee.setCreatedDate(parseDateOrDefault(cell3.toString(), dateFormatter));

        LocalDate date = ConvertDateUtil.parseDateOrDefault(cell3.toString(), dateFormatter);
        Integer year = date != null ? date.getYear() : null;
        String monthName = date != null ? date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : "";

        kseiSafekeepingFee.setCreatedDate(date);

        kseiSafekeepingFee.setMonth(monthName);

        kseiSafekeepingFee.setYear(year);

        Cell cell14 = row.getCell(14);
        kseiSafekeepingFee.setFeeDescription(cell14.toString());

        String customerCode = checkContainsSafekeeping(cell14.toString());
        kseiSafekeepingFee.setKseiSafeCode(customerCode);

        Cell cell15 = row.getCell(15);
        BigDecimal amountFee = parseBigDecimalOrDefault(cell15.toString());
        kseiSafekeepingFee.setAmountFee(amountFee);

        return kseiSafekeepingFee;
    }

    private static BigDecimal parseBigDecimalOrDefault(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            log.error("Parse BigDecimal is Failed: {}",  e.getMessage(), e);
            return null;
        }
    }

    private static LocalDate parseDateOrDefault(String value, DateTimeFormatter dateFormatter) {
        try {
            return LocalDate.parse(value, dateFormatter);
        } catch (Exception e) {
            log.error("Parse Local Date is Failed: {}", e.getMessage(), e);
            return null;
        }
    }

    private static String checkContainsSafekeeping(String inputString) {
        String result;
        if (containsKeyword(inputString)) {
            result = cleanedDescription(inputString);
        } else {
            result = "";
        }
        return result;
    }

    private static boolean containsKeyword(String input) {
        return input.contains("Safekeeping fee for account");
    }

    private static String cleanedDescription(String inputContainsSafekeeping) {
        return inputContainsSafekeeping.replace("Safekeeping fee for account", "").trim();
    }

}
