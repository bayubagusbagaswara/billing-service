package com.bayu.billingservice;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.BillingCustomerDTO;
import com.bayu.billingservice.service.BillingCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillingCustomerTest {

    @Autowired
    BillingCustomerService billingCustomerService;
    @Test
    @Order(1)
    void createKycCustomerCoreType1() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("15PCAP")
                .investmentManagementName("PT SETIABUDI INVESTMETN MANAGEMENT")
                .investmentManagementAddress("Setiabudi Atrium LT 5 Suites 501A<br/>Jl HR Rasuna Said Kav<br/>Kuningan Timur - Jakarta Selata<br/>Jakarta Selata")
                .productName("Reksa Dana Setiabudi Dana Obligasi Unggulan")
                .accountName("Reksa Dana Setiabudi Dana Obligasi Unggulan")
                .accountNumber("3607809104")
                .accountBank("PT Bank Danamon Indonesia, Tbk")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("0.03")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_1")
                .billingTemplate("TEMPLATE_3")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @Order(2)
    void createKycCustomerCoreType2() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("16NUII")
                .kseiSafeCode("")
                .minimumFee("500000")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_2")
                .billingTemplate("TEMPLATE_3")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @Order(3)
    void createKycCustomerCoreType3() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("14GIGC")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("GL 713017 CC 9207")
                .billingCategory("CORE")
                .billingType("TYPE_3")
                .billingTemplate("TEMPLATE_7")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("4A is Itama include VAT")
    @Order(4)
    void createKycCustomerCoreType4A() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("17OBAL")
                .kseiSafeCode("BDMN2OBAL00119")
                .minimumFee("")
                .customerFee("0.03")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_4")
                .billingTemplate("TEMPLATE_3")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("4B is EB without VAT")
    void createKycCustomerCoreType4B() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("17OBAL")
                .kseiSafeCode("BDMN2OBAL00119")
                .minimumFee("")
                .customerFee("0.03")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_4")
                .billingTemplate("TEMPLATE_5")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Type 5 and Type 6 without NPWP")
    @Order(5)
    void createKycCustomerCoreType5() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("14ZDEY")
                .kseiSafeCode("BDMN2ZDEY00134")
                .minimumFee("")
                .customerFee("0.05")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_5")
                .billingTemplate("TEMPLATE_1")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Type 5 and 6 With NPWP")
    @Order(6)
    void createKycCustomerCoreType6() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("14AJUT")
                .kseiSafeCode("BDMN2AJUT00157")
                .minimumFee("")
                .customerFee("0.05")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_6")
                .billingTemplate("TEMPLATE_4")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("7A is MUFG with aid 12MUFG")
    @Order(7)
    void createKycCustomerCoreType7A() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("12MUFG")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_7")
                .billingTemplate("TEMPLATE_2")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("7B is GUDH with aid 17GUDH")
    @Order(8)
    void createKycCustomerCoreType7B() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("17GUDH")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_7")
                .billingTemplate("TEMPLATE_2")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Alam Manunggal")
    @Order(9)
    void createKycCustomerCoreType8A() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("ALMAN")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_8")
                .billingTemplate("TEMPLATE_6")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Indo Infrastruktur")
    @Order(10)
    void createKycCustomerCoreType8B() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("INFRAS")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_8")
                .billingTemplate("TEMPLATE_6")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Mandala Kapital")
    @Order(11)
    void createKycCustomerCoreType8C() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("MANKAP")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_8")
                .billingTemplate("TEMPLATE_6")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @Order(12)
    void createKycCustomerCoreType9() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("13KONI")
                .kseiSafeCode("BDMN2KONI00111")
                .minimumFee("")
                .customerFee("0.05")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_9")
                .billingTemplate("TEMPLATE_3")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @Order(13)
    void createKycCustomerCoreType10() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("00NOIC")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("0.02")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_10")
                .billingTemplate("TEMPLATE_8")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Visiku")
    @Order(14)
    void createKycCustomerCoreType11A() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("VISIKU")
                .kseiSafeCode("")
                .minimumFee("5000000")
                .customerFee("0.35")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_11")
                .billingTemplate("TEMPLATE_3")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Fulus")
    @Order(15)
    void createKycCustomerCoreTyp11B() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("FULUS")
                .kseiSafeCode("")
                .minimumFee("5000000")
                .customerFee("0.35")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_11")
                .billingTemplate("TEMPLATE_3")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Frtaec")
    @Order(16)
    void createKycCustomerCoreType11C() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("FRTAEC")
                .kseiSafeCode("")
                .minimumFee("5000000")
                .customerFee("0.35")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_11")
                .billingTemplate("TEMPLATE_3")
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        assertNotNull(billingCustomerDTO.getId());
        assertEquals(request.getAid(), billingCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), billingCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), billingCustomerDTO.getBillingType());
    }

}
