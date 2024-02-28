package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.exception.CsvProcessingException;
import com.bayu.billingservice.model.KseiSafekeepingFee;
import com.bayu.billingservice.repository.KseiSafekeepingFeeRepository;
import com.bayu.billingservice.service.KseiSafekeepingFeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KseiSafekeepingFeeServiceImpl implements KseiSafekeepingFeeService {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private final KseiSafekeepingFeeRepository kseiSafekeepingFeeRepository;

    @Override
    public String readAndInsertToDB(String filePath) {
        log.info("File Path: {}", filePath);

        List<KseiSafekeepingFee> kseiSafekeepingFeeList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)))) {
            processSheets(workbook, kseiSafekeepingFeeList);
            kseiSafekeepingFeeRepository.saveAll(kseiSafekeepingFeeList);
            return "Excel data processed and saved successfully";
        } catch (IOException e) {
            log.error("Error reading the Excel file: {}", e.getMessage());
            return "Failed to process Excel file: " + e.getMessage();
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
            return "Failed to process Excel file: " + e.getMessage();
        }
    }

    private void processSheets(Workbook workbook, List<KseiSafekeepingFee> kseiSafekeepingFeeList) {
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();

        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            processRows(sheet, kseiSafekeepingFeeList);
        }
    }

    private void processRows(Sheet sheet, List<KseiSafekeepingFee> kseiSafekeepingFeeList) {
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
                log.error("Error processing a row: {}", e.getMessage());
                // You may choose to continue processing other rows or break the loop
                throw new CsvProcessingException("Failed to process Excel file: " + e.getMessage(), e);
            }
        }
    }

    private KseiSafekeepingFee createEntityFromRow(Row row) {
        KseiSafekeepingFee kseiSafekeepingFee = new KseiSafekeepingFee();
        Cell cell3 = row.getCell(2);
        kseiSafekeepingFee.setCreatedDate(parseDateOrDefault(cell3.toString(), dateFormatter));
        log.info("Created Date : {}", cell3.toString());

        Cell cell14 = row.getCell(14);
        kseiSafekeepingFee.setFeeDescription(cell14.toString());
        log.info("Fee Description : {}", cell14.toString());

        String feeAccount = checkContainsSafekeeping(cell14.toString());
        kseiSafekeepingFee.setFeeAccount(feeAccount);
        log.info("Fee Account : {}", feeAccount);

        Cell cell15 = row.getCell(15);
        BigDecimal amountFee = parseBigDecimalOrDefault(cell15.toString());
        kseiSafekeepingFee.setAmountFee(amountFee);
        log.info("Amount Fee : {}", amountFee);

        return kseiSafekeepingFee;
    }

    @Override
    public List<KseiSafekeepingFee> getAll() {
        return kseiSafekeepingFeeRepository.findAll();
    }

    @Override
    public KseiSafekeepingFee getByFeeAccount(String feeAccount) {
        return kseiSafekeepingFeeRepository.findByFeeAccount(feeAccount)
                .orElseThrow(() -> new RuntimeException("Data not found"));
    }

    private static BigDecimal parseBigDecimalOrDefault(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            log.error("Parse BigDecimal is Failed : " + e.getMessage(), e);
            return null;
        }
    }

    private static LocalDate parseDateOrDefault(String value, DateTimeFormatter dateFormatter) {
        try {
            return LocalDate.parse(value, dateFormatter);
        } catch (Exception e) {
            log.error("Parse Local Date is Failed : " + e.getMessage(), e);
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
        log.info("Input contains safekeeping : {}", inputContainsSafekeeping);
        String cleanedDescription = inputContainsSafekeeping.replace("Safekeeping fee for account", "").trim();
        log.info("Cleaned Description : {}", cleanedDescription);
        return cleanedDescription;
    }

}
