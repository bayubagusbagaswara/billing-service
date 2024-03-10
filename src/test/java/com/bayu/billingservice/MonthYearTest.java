package com.bayu.billingservice;

import com.bayu.billingservice.model.enumerator.MonthEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@SpringBootTest
class MonthYearTest {

    @Test
    void testInputMonthYear() {
        String input = "Nov 2023";
        String input1 = "November 2023";

        String[] split = input.split(" ");
        String month1 = split[0];
        String year1 = split[1];

        log.info("Month 1 : {}, Year 1 : {}", month1, year1);
    }

    @Test
    void testConvertInstant() {
        // Assuming you have an Instant object
        Instant currentInstant = Instant.now();
        Instant newInstant = currentInstant.plus(Duration.ofDays(14));

        // Define a formatter with the desired pattern and locale
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy")
//                .withLocale(java.util.Locale.forLanguageTag("id-ID"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy")
                .withLocale(Locale.forLanguageTag("id-ID"));


        // Set the time zone to Jakarta
        ZoneId jakartaZone = ZoneId.of("Asia/Jakarta");

        // Convert Instant to String
        String formattedString = formatter.format(currentInstant.atZone(jakartaZone));

        System.out.println("Formatted String: " + formattedString);
    }

}
