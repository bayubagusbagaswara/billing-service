package com.bayu.billingservice;

import com.bayu.billingservice.repository.ExchangeRateRepository;
import com.bayu.billingservice.service.ExchangeRateService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExchangeRateTest {

    private static final String CURRENCY_USD = "USD";

    @Autowired
    ExchangeRateService exchangeRateService;

    @Autowired
    ExchangeRateRepository exchangeRateRepository;
    @BeforeEach
    void cleanUpAndInsertData() {
        // Clean up existing data
        exchangeRateRepository.deleteAll();
    }

    @Test
    void insertData() {
        createExchangeRate("2024-02-23", "15590");
        createExchangeRate("2024-02-22", "15585");
        createExchangeRate("2024-02-21", "15630");
        createExchangeRate("2024-02-20", "15655");
        createExchangeRate("2024-02-19", "15625");
        createExchangeRate("2024-02-16", "15615");
        createExchangeRate("2024-02-15", "15615");
    }

    void createExchangeRate(String date, String value) {
        CreateExchangeRateRequest request = CreateExchangeRateRequest.builder()
                .date(date)
                .currency(CURRENCY_USD)
                .value(value)
                .build();

        ExchangeRateDTO exchangeRateDTO = exchangeRateService.create(request);
        assertNotNull(exchangeRateDTO.getId());
        assertEquals(exchangeRateDTO.getDate(), request.getDate());
    }

}
