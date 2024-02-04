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

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
    private static final DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("d/M/yyyy");

    public static List<SkTransaction> mapCsvSkTransaction(List<String[]> rows) {
        List<SkTransaction> skTransactionList = new ArrayList<>();
        log.info("[Start Map CSV] SKTran Rows : {}", rows.size());

        for (String[] row : rows) {
            SkTransaction skTransaction = SkTransaction.builder()
                    .tradeId(trimString(row[0]))
                    .portfolioCode(trimString(row[1]))
                    .securityType(trimString(row[2]))
                    .securityShortName(trimString(row[3]))
                    .securityName(trimString(row[4]))
                    .type(trimString(row[5]))
                    .tradeDate(parseDateOrDefault(row[6], dateFormatter, null))
                    .settlementDate(parseDateOrDefault(row[7], dateFormatter, null))
                    .amount(parseBigDecimalOrDefault(row[8].replaceAll(".", ""), null))
                    .currency(trimString(row[9]))
                    .deleteStatus(trimString(row[10]))
                    .system(trimString(row[11]))
                    .sid(trimString(row[12]))
                    .remark(trimString(row[13]))
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
                    .aid(trimString(row[2]))
                    .securityName(trimString(row[3]))
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
                    .aid(trimString(row[2]))
                    .securityName(trimString(row[3]))
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

    private static String trimString(String value) {
        return value != null ? value.trim() : null;
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
