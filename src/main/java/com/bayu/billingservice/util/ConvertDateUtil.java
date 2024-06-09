package com.bayu.billingservice.util;

import com.bayu.billingservice.dto.MonthYearDTO;
import com.bayu.billingservice.dto.billing.BillingContextDate;
import com.bayu.billingservice.exception.GeneralException;
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
            return LocalDate.parse(value, dateTimeFormatter);
        } catch (Exception e) {
            log.error("Parse Date is Failed: {}", e.getMessage(), e);
            return null;
        }
    }

    public LocalDate getLatestDateOfMonthYear(String monthYear) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(APPEND_PATTERN)
                .toFormatter(getLocaleEN());

        TemporalAccessor temporalAccessor = formatter.parse(monthYear);
        LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));

        return parsedDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public LocalDate getFirstDateOfMonthYear(String monthYear) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(APPEND_PATTERN)
                .toFormatter(getLocaleEN());

        TemporalAccessor temporalAccessor = formatter.parse(monthYear);
        LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));

        return parsedDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    public Map<String, String> extractMonthYearInformation(String monthYear) {
        LocalDate firstDateOfMonthYear = getFirstDateOfMonthYear(monthYear);

        // Month
        int monthValue = firstDateOfMonthYear.getMonthValue();
        String monthFullName = firstDateOfMonthYear.getMonth().getDisplayName(TextStyle.FULL, getLocaleEN());

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

    public String convertToRange3Months(String[][] previousMonthsAndYears) {
        String currentlyMonth = previousMonthsAndYears[0][0]; // October
        String currentlyYear = previousMonthsAndYears[0][1]; // 2023

        String latestMonth = previousMonthsAndYears[2][0]; // August
        String latestYear = previousMonthsAndYears[2][1]; // 2023

        log.info("Currently Month '{}', Currently Year '{}', Latest Month '{}', Latest Year '{}'",
                currentlyMonth, currentlyYear, latestMonth, latestYear);

        String formatCurrentlyMonth = currentlyMonth.substring(0, 3);
        String formatLatestMonth = latestMonth.substring(0, 3);

        // Currently Month 'October', Currently Year '2023', Latest Month 'August', Latest Year '2023'
        // How to convert Aug 2023 - Oct 2023
        return formatLatestMonth + " " + latestYear + " - " + formatCurrentlyMonth + " " + currentlyYear;
    }

    public static class MonthYearQuery implements TemporalQuery<LocalDate> {
        @Override
        public LocalDate queryFrom(TemporalAccessor temporal) {
            int year = temporal.get(ChronoField.YEAR);
            int month = temporal.get(ChronoField.MONTH_OF_YEAR);
            return LocalDate.of(year, month, 1); // Day set to 1 for the first day of the month
        }
    }

    public String[] convertToYearMonthFormat(String monthYear) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(APPEND_PATTERN)
                .toFormatter(getLocaleEN());

        TemporalAccessor temporalAccessor = formatter.parse(monthYear);
        LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));

        // Format the parsed date into the desired output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH);
        // Split the formatted date string
        String formattedDate = parsedDate.format(outputFormatter);

        // Split the formatted date string
        return formattedDate.split("-");
    }

    public static String convertInstantToString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy")
                .withLocale(Locale.ENGLISH);

        ZoneId jakartaZone = ZoneId.of("Asia/Jakarta");

        return formatter.format(instant.atZone(jakartaZone));
    }

    public static String convertInstantToStringPlus14Days(Instant instant) {
        Instant newInstant = instant.plus(Duration.ofDays(14));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy")
                .withLocale(Locale.ENGLISH);
//                .withLocale(Locale.forLanguageTag("id-ID"));

        ZoneId jakartaZone = ZoneId.of("Asia/Jakarta");

        return formatter.format(newInstant.atZone(jakartaZone));
    }

    public static Locale getLocaleID() {
        return new Locale("id", "ID");
    }

    public static Locale getLocaleEN() {
        return Locale.ENGLISH;
    }

    public static String[][] getPreviousMonthsAndYears(String month, int year) {
        // Month sudah dalam bentuk November or October etc
        String[][] result = new String[3][2];

        String monthInput = month.toUpperCase();
        // IndonesianMonth userMonth = getIndonesianMonth(monthInput);

        if (!monthInput.isEmpty() && isValidYear(year)) {
            // Year Month harus parameter Month
            Month userMonth = Month.valueOf(monthInput.toUpperCase()); // Convert to uppercase for case-insensitivity
            YearMonth userYearMonth = YearMonth.of(year, userMonth);

            String currentlyMonth = userYearMonth.getMonth().getDisplayName(TextStyle.FULL, ConvertDateUtil.getLocaleEN());
            int currentlyYear = userYearMonth.getYear();

            YearMonth previousMonth1 = userYearMonth.minusMonths(1);
            YearMonth previousMonth2 = userYearMonth.minusMonths(2);

            String previousMonthName1 = previousMonth1.getMonth().getDisplayName(TextStyle.FULL, ConvertDateUtil.getLocaleEN());
            String previousMonthName2 = previousMonth2.getMonth().getDisplayName(TextStyle.FULL, ConvertDateUtil.getLocaleEN());

            int previousYear1 = previousMonth1.getYear();
            int previousYear2 = previousMonth2.getYear();

            result[0][0] = currentlyMonth;
            result[0][1] = String.valueOf(currentlyYear);
            result[1][0] = previousMonthName1;
            result[1][1] = String.valueOf(previousYear1);
            result[2][0] = previousMonthName2;
            result[2][1] = String.valueOf(previousYear2);

        } else {
            throw new IllegalArgumentException("Invalid month or year.");
        }

        return result;
    }

    private static boolean isValidYear(int year) {
        return year > 0; // Assuming years must be positive
    }

    public Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZoneId jakartaZoneId = ZoneId.of(timeZone);
        return Date.from(localDateTime.atZone(jakartaZoneId).toInstant());
    }

    public Date getDate() {
        return convertLocalDateTimeToDate(LocalDateTime.now());
    }

    public Map<String, String> getMonthMinus1() {
        try {
            LocalDate currentDate = LocalDate.now();
            int currentMonth = currentDate.getMonthValue();
            int currentYear = currentDate.getYear();

            int newMonth = currentMonth - 1;
            if (newMonth == 0) {
                newMonth = 12;
                currentYear--;
            }

            LocalDate previousMonthYear = LocalDate.of(currentYear, newMonth, 1);
            String monthName = previousMonthYear.getMonth().getDisplayName(TextStyle.FULL, getLocaleEN());
            int monthValue = previousMonthYear.getMonth().getValue();
            String formattedMonthValue = (monthValue < 10) ? "0" + monthValue : String.valueOf(monthValue);
            int year = previousMonthYear.getYear();

            Map<String, String> monthYear = new HashMap<>();
            monthYear.put("monthName", monthName);
            monthYear.put("monthValue", formattedMonthValue);
            monthYear.put("year", String.valueOf(year));
            return monthYear;
        } catch (Exception e) {
            log.error("Error when get month minus 1: {}", e.getMessage(), e);
            throw new GeneralException(e.getMessage());
        }
    }

    public Map<String, String> getMonthNow() {
        try {
            LocalDate currentDate = LocalDate.now();
            String monthName = currentDate.getMonth().getDisplayName(TextStyle.FULL, getLocaleEN());
            int year = currentDate.getYear();

            Map<String, String> monthYear = new HashMap<>();
            monthYear.put("monthName", monthName);
            monthYear.put("year", String.valueOf(year));
            return monthYear;
        } catch (Exception e) {
            log.error("Error when get month now: {}", e.getMessage(), e);
            throw new GeneralException(e.getMessage());
        }
    }

    public BillingContextDate getBillingContextDate(Instant dateNow) {
        try {
            Map<String, String> monthMinus1 = getMonthMinus1();
//            String monthNameMinus1 = monthMinus1.get("monthName"); // month Name ini diganti dengan April
//            int yearMinus1 = Integer.parseInt(monthMinus1.get("year")); // year ini diganti dengan 2024

            String monthNameMinus1 = "May";
            Integer yearMinus1 = 2024;

            Map<String, String> monthNow = getMonthNow();
            String monthNameNow = monthNow.get("monthName");
            int yearNow = Integer.parseInt(monthNow.get("year"));

            String billingPeriod = monthNameMinus1 + " " + yearMinus1;

            return new BillingContextDate(dateNow, monthNameMinus1, yearMinus1, monthNameNow, yearNow, billingPeriod);
        } catch (Exception e) {
            log.error("Error when get billing context date: {}", e.getMessage(), e);
            throw new GeneralException("Error when get billing context date: " + e.getMessage());
        }
    }

    public MonthYearDTO parseBillingPeriodToLocalDate(String billingPeriod) {
        Map<String, String> stringMap = extractMonthYearInformation(billingPeriod);

        return new MonthYearDTO(stringMap.get("monthName"), stringMap.get("monthValue"), Integer.parseInt(stringMap.get("year")));
    }

}
