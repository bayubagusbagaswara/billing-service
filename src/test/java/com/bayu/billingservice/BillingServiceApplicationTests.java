package com.bayu.billingservice;

import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Slf4j
@SpringBootTest
class BillingServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testDisplayMonthFullName() {
		// Create a LocalDate object
		LocalDate localDate = LocalDate.now();

		// Get the month from the LocalDate object
		Month month = localDate.getMonth();

		// Get the full month name in the default locale
		String fullMonthName = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		System.out.println("Full Month Name: " + fullMonthName);
	}

	@Test
	void testToUpperCase() {
		String type1 = "Type 1";
		String upperCase = type1.replaceAll("\\s", "_").toUpperCase();

		System.out.println(upperCase);
	}

	@Test
	void testDate() {
		String inputMonthYear = "February 2024"; // Example input: "YYYY-MM"

		Map<String, String> monthYearMap = extractMonthYearInformation(inputMonthYear);

		// Displaying the result
		System.out.println("Month: " + monthYearMap.get("month"));
		System.out.println("Year: " + monthYearMap.get("year"));
		System.out.println("Month Name: " + monthYearMap.get("monthName"));
	}

	public static Map<String, String> extractMonthYearInformation(String monthYear) {
		LocalDate latestDateOfMonthYear = getLatestDateOfMonthYear(monthYear);

		// Month
		int monthValue = latestDateOfMonthYear.getMonthValue();
		String monthFullName = latestDateOfMonthYear.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

		// Year
		int year = latestDateOfMonthYear.getYear();

		Map<String, String> monthYearMap = new HashMap<>();
		monthYearMap.put("month", String.valueOf(monthValue));
		monthYearMap.put("year", String.valueOf(year));
		monthYearMap.put("monthName", monthFullName);

		return monthYearMap;
	}

	private static LocalDate getLatestDateOfMonthYear(String monthYear) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("[MMM ][MMMM ]yyyy")
				.toFormatter(Locale.ENGLISH);

		TemporalAccessor temporalAccessor = formatter.parse(monthYear);
		LocalDate parsedDate = LocalDate.from(new ConvertDateUtil.MonthYearQuery().queryFrom(temporalAccessor));

		LocalDate latestDateOfMonth = parsedDate.with(TemporalAdjusters.lastDayOfMonth());
		log.info("Latest Date of Month Year : {}", latestDateOfMonth);
		return latestDateOfMonth;
	}

	@Test
	void testParseMonthYear() {
		String input1 = "Nov 2023";
		String input2 = "November 2023";


		try {
			String convertedDate1 = convertToYearMonthFormat(input1);
			System.out.println("Converted Date 1: " + convertedDate1);
			String[] split1 = convertedDate1.split("-");
			System.out.println("Split 0 : " + split1[0]);
			System.out.println("Split 1 : " + split1[1]);

			String convertedDate2 = convertToYearMonthFormat(input2);
			System.out.println("Converted Date 2: " + convertedDate2);
		} catch (Exception e) {
			System.out.println("Error: Unable to parse the input date.");
		}
	}

	private static String convertToYearMonthFormat(String input) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("[MMM ][MMMM ]yyyy")
				.toFormatter(Locale.ENGLISH);

		TemporalAccessor temporalAccessor = formatter.parse(input);
		LocalDate parsedDate = LocalDate.from(new ConvertDateUtil.MonthYearQuery().queryFrom(temporalAccessor));

		// Format the parsed date into the desired output format
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH);
		return parsedDate.format(outputFormatter);
	}
}
