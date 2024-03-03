package com.bayu.billingservice;

import com.bayu.billingservice.model.enumerator.MonthEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
}
