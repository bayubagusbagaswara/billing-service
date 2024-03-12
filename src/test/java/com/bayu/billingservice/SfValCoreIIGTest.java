package com.bayu.billingservice;

import com.bayu.billingservice.service.SfValCoreIIGService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

@SpringBootTest
class SfValCoreIIGTest {

    @Autowired
    SfValCoreIIGService sfValCoreIIGService;


    @Test
    void testCalculateDivide100() {

        // Input string
        String inputString = "0.05";

        // Convert the string to a BigDecimal
        BigDecimal inputBigDecimal = new BigDecimal(inputString);

        // Divide by 100
        BigDecimal result = inputBigDecimal.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);

        // Display the result
        System.out.println("Result: " + result);
    }

    @Test
    @DisplayName("Data Alama Manunggal")
    void insertData1() {


    }

    @Test
    @DisplayName("Data Indo Infrastruktur")
    void insertData2() {

    }

    @Test
    @DisplayName("Data Mandala Kapital")
    void insertData3() {

    }
}
