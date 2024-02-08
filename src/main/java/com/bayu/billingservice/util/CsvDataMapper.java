package com.bayu.billingservice.util;

import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SfValRgMonthly;
import com.bayu.billingservice.model.SkTransaction;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
@Slf4j
public class CsvDataMapper {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MMM-yy");
    private static final DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("d/M/yyyy");

    public static List<SkTransaction> mapCsvSkTransaction(List<String[]> rows) {
        List<SkTransaction> skTransactionList = new ArrayList<>();
        log.info("[Start Map CSV] SKTran Rows : {}", rows.size());

        for (String[] row : rows) {
            SkTransaction skTransaction = SkTransaction.builder()
                    .tradeId(processString(row[0]))
                    .portfolioCode(processString(row[1]))
                    .securityType(processString(row[2]))
                    .securityShortName(processString(row[3]))
                    .securityName(processString(row[4]))
                    .type(processString(row[5]))
                    .tradeDate(parseDateOrDefault(row[6], dateFormatter, null))
                    .settlementDate(parseDateOrDefault(row[7], dateFormatter, null))
                    .amount(parseBigDecimalOrDefault(row[8].replace(".", ""), null))
                    .currency(processString(row[9]))
                    .deleteStatus(processString(row[10]))
                    .settlementSystem(processString(row[12]))
                    .sid(processString(row[14]))
                    .remark(removeWhitespaceBetweenCharacters(row[15]))
                    .build();

            skTransactionList.add(skTransaction);
        }

        log.info("[Finish Map CSV] SKTran Size : {}", skTransactionList.size());
        return skTransactionList;
    }

    public static List<SfValRgDaily> mapCsvSfValRgDaily(List<String[]> rows) {
        List<SfValRgDaily> sfValRgDailyList = new ArrayList<>();
        log.info("[Start Map CSV] SfVal RG Daily Rows : {}", rows.size());
        for (String[] row : rows) {
            SfValRgDaily sfValRgDaily = SfValRgDaily.builder()
                    .batch(parseIntOrDefault(row[0]))
                    .date(parseDateOrDefault(row[1], dateTimeFormatter1, null))
                    .aid(processString(row[2]))
                    .securityName(processString(row[3]))
                    .faceValue(parseBigDecimalOrDefault(row[4], null))
                    .marketPrice(row[5])
                    .marketValue(parseBigDecimalOrDefault(row[6], null))
                    .estimationSafekeepingFee(parseBigDecimalOrDefault(row[7], null))
                    .build();
            sfValRgDailyList.add(sfValRgDaily);
        }

        log.info("[Finish Map CSV] SfVal RG Daily Size : {}", sfValRgDailyList.size());
        return sfValRgDailyList;
    }

    public static List<SfValRgMonthly> mapCsvSfValRgMonthly(List<String[]> rows) {
        List<SfValRgMonthly> sfValRgMonthlyList = new ArrayList<>();
        log.info("[Start Map CSV] SfVal RG Monthly Rows : {}", rows.size());

        for (String[] row : rows) {
            SfValRgMonthly sfValRgMonthly = SfValRgMonthly.builder()
                    .batch(parseIntOrDefault(row[0]))
                    .date(parseDateOrDefault(row[1], dateTimeFormatter1, null))
                    .aid(processString(row[2]))
                    .securityName(processString(row[3]))
                    .faceValue(parseBigDecimalOrDefault(row[4], null))
                    .marketPrice(row[5])
                    .marketValue(parseBigDecimalOrDefault(row[6], null))
                    .estimationSafekeepingFee(parseBigDecimalOrDefault(row[7], null))
                    .build();
            sfValRgMonthlyList.add(sfValRgMonthly);
        }

        log.info("[Finish Map CSV] SfVal RG Monthly Size : {}", sfValRgMonthlyList.size());
        return sfValRgMonthlyList;
    }

    private static String processString(String value) {
        // Check if the value is null and return a default value if needed
        String processedValue = !value.isEmpty() ? value : "";

        // Trim the string if it is not null
        processedValue = processedValue.trim();

        return processedValue;
    }

    private static String removeWhitespaceBetweenCharacters(String input) {
        // Replace all whitespace between characters with an empty string
        return input.replaceAll("\\s+", "").trim();
    }

    private static Integer parseIntOrDefault(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static LocalDate parseDateOrDefault(String value, DateTimeFormatter dateFormatter, LocalDate defaultValue) {
        try {
            return LocalDate.parse(value, dateFormatter);
        } catch (Exception e) {
            log.error("Parse Date is Failed : " + e.getMessage(), e);
            return defaultValue;
        }
    }

    private static BigDecimal parseBigDecimalOrDefault(String value, BigDecimal defaultValue) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            log.error("Parse BigDecimal is Failed : " + e.getMessage(), e);
            return defaultValue;
        }
    }

}
