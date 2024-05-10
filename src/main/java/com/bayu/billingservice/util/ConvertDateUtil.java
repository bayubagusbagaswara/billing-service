package com.bayu.billingservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class ConvertDateUtil {

    @Value("${spring.jackson.time-zone}")
    private String timeZone;

    private static final String APPEND_PATTERN = "[MMM ][MMMM ]yyyy";

    public static LocalDate parseDateOrDefault(String value, DateTimeFormatter dateTimeFormatter) {
        try {
            LocalDate parse = LocalDate.parse(value, dateTimeFormatter);
            log.info("Result Parse Date : {}", parse);
            return parse;
        } catch (Exception e) {
            log.error("Parse Date is Failed : {}", e.getMessage(), e);
            return null;
        }
    }

    public LocalDate getLatestDateOfMonthYear(String monthYear) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(APPEND_PATTERN)
                .toFormatter(getLocaleID());

        TemporalAccessor temporalAccessor = formatter.parse(monthYear);
        LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));

        LocalDate latestDateOfMonth = parsedDate.with(TemporalAdjusters.lastDayOfMonth());
        log.info("Latest Date of Month Year : {}", latestDateOfMonth);

        return latestDateOfMonth;
    }

    public LocalDate getFirstDateOfMonthYear(String monthYear) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(APPEND_PATTERN)
                .toFormatter(getLocaleID());

        TemporalAccessor temporalAccessor = formatter.parse(monthYear);
        LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));

        LocalDate firstDateOfMonth = parsedDate.with(TemporalAdjusters.firstDayOfMonth());
        log.info("First Date of Month Year : {}", firstDateOfMonth);

        return firstDateOfMonth;
    }

    public Map<String, String> extractMonthYearInformation(String monthYear) {
        LocalDate firstDateOfMonthYear = getFirstDateOfMonthYear(monthYear);

        // Month
        int monthValue = firstDateOfMonthYear.getMonthValue();
        String monthFullName = firstDateOfMonthYear.getMonth().getDisplayName(TextStyle.FULL, getLocaleID());
        String formattedMonth = (monthValue < 10) ? "0" + monthValue : String.valueOf(monthValue);

        // Year
        int year = firstDateOfMonthYear.getYear();

        Map<String, String> monthYearMap = new HashMap<>();
        monthYearMap.put("month", String.valueOf(monthValue));
        monthYearMap.put("year", String.valueOf(year));
        monthYearMap.put("monthName", monthFullName);
        monthYearMap.put("monthValue", formattedMonth);

        return monthYearMap;
    }

    public static class MonthYearQuery implements TemporalQuery<LocalDate> {
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
        LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));

        // Format the parsed date into the desired output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH);
        // Split the formatted date string
        String formattedDate = parsedDate.format(outputFormatter);

        // Split the formatted date string
        return formattedDate.split("-");
    }

    public String convertInstantToString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy")
                .withLocale(Locale.forLanguageTag("id-ID"));

        ZoneId jakartaZone = ZoneId.of(timeZone);

        return formatter.format(instant.atZone(jakartaZone));
    }

    public String convertInstantToStringPlus14Days(Instant instant) {
        Instant newInstant = instant.plus(Duration.ofDays(14));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy")
                .withLocale(Locale.forLanguageTag("id-ID"));

        ZoneId jakartaZone = ZoneId.of(timeZone);

        String formattedString = formatter.format(newInstant.atZone(jakartaZone));
        log.info("Formatted Instant to String : {}", formattedString);

        return formattedString;
    }

    private static Locale getLocaleID() {
//        return new Locale("id", "ID");
        return Locale.getDefault();
    }

    public Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        log.info("Time Zone: {}", timeZone);
        ZoneId jakartaZoneId = ZoneId.of(timeZone);
        return Date.from(localDateTime.atZone(jakartaZoneId).toInstant());
    }

    public Date getDate() {
        return convertLocalDateTimeToDate(LocalDateTime.now());
    }
}
