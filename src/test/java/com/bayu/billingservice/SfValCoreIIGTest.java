package com.bayu.billingservice;

import com.bayu.billingservice.service.SfValCoreIIGService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SfValCoreIIGTest {

    @Autowired
    SfValCoreIIGService sfValCoreIIGService;

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
