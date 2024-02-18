package com.bayu.billingservice.util;

import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SfValRgMonthly;
import com.bayu.billingservice.model.SkTransaction;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@UtilityClass
@Slf4j
public class CsvDataMapper {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d-MMM-yy");
    private static final DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("d/M/yyyy");

    public static List<SkTransaction> mapCsvSkTransaction(List<String[]> rows) {
        List<SkTransaction> skTransactionList = new ArrayList<>();
        log.info("[Start Map CSV] SKTran Rows : {}", rows.size());

        for (String[] row : rows) {
            LocalDate date = ConvertDateUtil.parseDateOrDefault(row[7], dateTimeFormatter);
            Integer year = date != null ? date.getYear() : null;
            String monthName = date != null ? date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : null;

            SkTransaction skTransaction = SkTransaction.builder()
                    .tradeId(StringUtil.processString(row[0]))
                    .portfolioCode(StringUtil.processString(row[1]))
                    .securityType(StringUtil.processString(row[2]))
                    .securityShortName(StringUtil.processString(row[3]))
                    .securityName(StringUtil.processString(row[4]))
                    .type(StringUtil.processString(row[5]))
                    .tradeDate(ConvertDateUtil.parseDateOrDefault(row[6], dateTimeFormatter))
                    .settlementDate(date)
                    .month(monthName)
                    .year(year)
                    .amount(ConvertBigDecimalUtil.parseBigDecimalOrDefault(row[8].replace(".", "")))
                    .currency(StringUtil.processString(row[9]))
                    .deleteStatus(StringUtil.processString(row[10]))
                    .settlementSystem(StringUtil.processString(row[12]))
                    .sid(StringUtil.processString(row[14]))
                    .remark(StringUtil.removeWhitespaceBetweenCharacters(row[15]))
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
            LocalDate date = ConvertDateUtil.parseDateOrDefault(row[1], dateTimeFormatter1);
            Integer year = date != null ? date.getYear() : null;
            String monthName = date != null ? date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : null;

            SfValRgDaily sfValRgDaily = SfValRgDaily.builder()
                    .batch(ConvertIntegerUtil.parseIntOrDefault(row[0]))
                    .date(ConvertDateUtil.parseDateOrDefault(row[1], dateTimeFormatter1))
                    .month(monthName)
                    .year(year)
                    .aid(StringUtil.processString(row[2]))
                    .securityName(StringUtil.processString(row[3]))
                    .faceValue(ConvertBigDecimalUtil.parseBigDecimalOrDefault(row[4]))
                    .marketPrice(row[5])
                    .marketValue(ConvertBigDecimalUtil.parseBigDecimalOrDefault(row[6]))
                    .estimationSafekeepingFee(ConvertBigDecimalUtil.parseBigDecimalOrDefault(row[7]))
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
            LocalDate date = ConvertDateUtil.parseDateOrDefault(row[1], dateTimeFormatter1);
            Integer year = date != null ? date.getYear() : null;
            String monthName = date != null ? date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : null;

            SfValRgMonthly sfValRgMonthly = SfValRgMonthly.builder()
                    .batch(ConvertIntegerUtil.parseIntOrDefault(row[0]))
                    .date(date)
                    .month(monthName)
                    .year(year)
                    .aid(StringUtil.processString(row[2]))
                    .securityName(StringUtil.processString(row[3]))
                    .faceValue(ConvertBigDecimalUtil.parseBigDecimalOrDefault(row[4]))
                    .marketPrice(row[5])
                    .marketValue(ConvertBigDecimalUtil.parseBigDecimalOrDefault(row[6]))
                    .estimationSafekeepingFee(ConvertBigDecimalUtil.parseBigDecimalOrDefault(row[7]))
                    .build();

            sfValRgMonthlyList.add(sfValRgMonthly);
        }

        log.info("[Finish Map CSV] SfVal RG Monthly Size : {}", sfValRgMonthlyList.size());
        return sfValRgMonthlyList;
    }

}
