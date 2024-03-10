package com.bayu.billingservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@UtilityClass
public class ConvertDateUtil {

    private static final String APPEND_PATTERN = "[MMM ][MMMM ]yyyy";

    public static LocalDate parseDateOrDefault(String value, DateTimeFormatter dateTimeFormatter) {
        try {
            LocalDate parse = LocalDate.parse(value, dateTimeFormatter);
            log.info("Result Parse Date : {}", parse);
            return parse;
        } catch (Exception e) {
            log.error("Parse Date is Failed : " + e.getMessage(), e);
            return null;
        }
    }

    public static LocalDate getLatestDateOfMonthYear(String monthYear) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(APPEND_PATTERN)
                .toFormatter(new Locale("id", "ID"));

        TemporalAccessor temporalAccessor = formatter.parse(monthYear);
        LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));

        LocalDate latestDateOfMonth = parsedDate.with(TemporalAdjusters.lastDayOfMonth());
        log.info("Latest Date of Month Year : {}", latestDateOfMonth);

        return latestDateOfMonth;
    }

    public static LocalDate getFirstDateOfMonthYear(String monthYear) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(APPEND_PATTERN)
                .toFormatter(new Locale("id", "ID"));

        TemporalAccessor temporalAccessor = formatter.parse(monthYear);
        LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));

        LocalDate firstDateOfMonth = parsedDate.with(TemporalAdjusters.firstDayOfMonth());
        log.info("First Date of Month Year : {}", firstDateOfMonth);

        return firstDateOfMonth;
    }

    public static Map<String, String> extractMonthYearInformation(String monthYear) {
        LocalDate latestDateOfMonthYear = getLatestDateOfMonthYear(monthYear);

        // Month
        int monthValue = latestDateOfMonthYear.getMonthValue();
        String monthFullName = latestDateOfMonthYear.getMonth().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));

        // Year
        int year = latestDateOfMonthYear.getYear();

        Map<String, String> monthYearMap = new HashMap<>();
        monthYearMap.put("month", String.valueOf(monthValue));
        monthYearMap.put("year", String.valueOf(year));
        monthYearMap.put("monthName", monthFullName);

        return monthYearMap;
    }

    public class MonthYearQuery implements TemporalQuery<LocalDate> {
        @Override
        public LocalDate queryFrom(TemporalAccessor temporal) {
            int year = temporal.get(ChronoField.YEAR);
            int month = temporal.get(ChronoField.MONTH_OF_YEAR);
            return LocalDate.of(year, month, 1); // Day set to 1 for the first day of the month
        }
    }

    public static String[] convertToYearMonthFormat(String monthYear) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(APPEND_PATTERN)
                .toFormatter(new Locale("id", "ID"));

        TemporalAccessor temporalAccessor = formatter.parse(monthYear);
        LocalDate parsedDate = LocalDate.from(new ConvertDateUtil.MonthYearQuery().queryFrom(temporalAccessor));

        // Format the parsed date into the desired output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH);
        // Split the formatted date string
        String formattedDate = parsedDate.format(outputFormatter);

        // Split the formatted date string
        return formattedDate.split("-");
    }

    public static String convertInstantToString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy")
                .withLocale(Locale.forLanguageTag("id-ID"));

        ZoneId jakartaZone = ZoneId.of("Asia/Jakarta");

        String formattedString = formatter.format(instant.atZone(jakartaZone));
        log.info("Formatted Instant to String : {}", formattedString);

        return formattedString;
    }

    public static String convertInstantToStringPlus14Days(Instant instant) {
        Instant newInstant = instant.plus(Duration.ofDays(14));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy")
                .withLocale(Locale.forLanguageTag("id-ID"));

        ZoneId jakartaZone = ZoneId.of("Asia/Jakarta");

        String formattedString = formatter.format(newInstant.atZone(jakartaZone));
        log.info("Formatted Instant to String : {}", formattedString);

        return formattedString;
    }

}
