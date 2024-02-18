package com.bayu.billingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

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
}
