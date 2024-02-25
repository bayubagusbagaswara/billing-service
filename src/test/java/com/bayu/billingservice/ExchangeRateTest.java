package com.bayu.billingservice;

import com.bayu.billingservice.dto.exchangerate.CreateExchangeRateRequest;
import com.bayu.billingservice.dto.exchangerate.ExchangeRateDTO;
import com.bayu.billingservice.service.ExchangeRateService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExchangeRateTest {

    private static final String CURRENCY_USD = "USD";

    @Autowired
    ExchangeRateService exchangeRateService;

    @ParameterizedTest
    @CsvSource({
            "2024-02-23, 15590",
            "2024-02-22, 15585",
            "2024-02-21, 15630",
            "2024-02-20, 15655",
            "2024-02-19, 15625",
            "2024-02-16, 15615",
            "2024-02-15, 15615"
    })
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
