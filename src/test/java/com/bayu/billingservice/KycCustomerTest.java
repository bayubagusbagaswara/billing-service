package com.bayu.billingservice;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;
import com.bayu.billingservice.service.KycCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KycCustomerTest {

    @Autowired
    KycCustomerService kycCustomerService;
    @Test
    @Order(1)
    void createKycCustomerCoreType1() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("15PCAP")
                .kseiSafeCode("")
                .minimumFee("")
                .customerSafekeepingFee("0.03")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_1")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @Order(2)
    void createKycCustomerCoreType2() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("16NUII")
                .kseiSafeCode("")
                .minimumFee("500000")
                .customerSafekeepingFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_2")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @Order(3)
    void createKycCustomerCoreType3() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("14GIGC")
                .kseiSafeCode("")
                .minimumFee("")
                .customerSafekeepingFee("")
                .journal("GL 713017 CC 9207")
                .billingCategory("CORE")
                .billingType("TYPE_3")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("4A is Itama include VAT")
    @Order(4)
    void createKycCustomerCoreType4A() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("17OBAL")
                .kseiSafeCode("BDMN2OBAL00119")
                .minimumFee("")
                .customerSafekeepingFee("0.03")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_4A")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("4B is EB without VAT")
    @Order(5)
    void createKycCustomerCoreType4B() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("17OBAL")
                .kseiSafeCode("BDMN2OBAL00119")
                .minimumFee("")
                .customerSafekeepingFee("0.03")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_4B")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @Order(6)
    void createKycCustomerCoreType5() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("14ZDEY")
                .kseiSafeCode("BDMN2ZDEY00134")
                .minimumFee("")
                .customerSafekeepingFee("0.05")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_5")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @Order(7)
    void createKycCustomerCoreType6() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("14AJUT")
                .kseiSafeCode("BDMN2AJUT00157")
                .minimumFee("")
                .customerSafekeepingFee("0.05")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_6")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("7A is MUFG with aid 12MUFG")
    @Order(8)
    void createKycCustomerCoreType7A() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("12MUFG")
                .kseiSafeCode("")
                .minimumFee("")
                .customerSafekeepingFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_7")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("7B is GUDH with aid 17GUDH")
    @Order(9)
    void createKycCustomerCoreType7B() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("17GUDH")
                .kseiSafeCode("")
                .minimumFee("")
                .customerSafekeepingFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_7")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Alam Manunggal")
    @Order(10)
    void createKycCustomerCoreType8A() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("ALMAN")
                .kseiSafeCode("")
                .minimumFee("")
                .customerSafekeepingFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_8")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Indo Infrastruktur")
    @Order(11)
    void createKycCustomerCoreType8B() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("INFRAS")
                .kseiSafeCode("")
                .minimumFee("")
                .customerSafekeepingFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_8")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Mandala Kapital")
    @Order(12)
    void createKycCustomerCoreType8C() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("MANKAP")
                .kseiSafeCode("")
                .minimumFee("")
                .customerSafekeepingFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_8")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @Order(13)
    void createKycCustomerCoreType9() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("13KONI")
                .kseiSafeCode("BDMN2KONI00111")
                .minimumFee("")
                .customerSafekeepingFee("0.05")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_9")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @Order(14)
    void createKycCustomerCoreType10() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("00NOIC")
                .kseiSafeCode("")
                .minimumFee("")
                .customerSafekeepingFee("0.02")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_10")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Visiku")
    @Order(15)
    void createKycCustomerCoreType11A() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("VISIKU")
                .kseiSafeCode("")
                .minimumFee("5000000")
                .customerSafekeepingFee("0.35")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_11")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Fulus")
    @Order(16)
    void createKycCustomerCoreTyp11B() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("FULUS")
                .kseiSafeCode("")
                .minimumFee("5000000")
                .customerSafekeepingFee("0.35")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_11")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

    @Test
    @DisplayName("Frtaec")
    @Order(17)
    void createKycCustomerCoreType11C() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("FRTAEC")
                .kseiSafeCode("")
                .minimumFee("5000000")
                .customerSafekeepingFee("0.35")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_11")
                .build();

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        assertNotNull(kycCustomerDTO.getId());
        assertEquals(request.getAid(), kycCustomerDTO.getAid());
        assertEquals(request.getBillingCategory(), kycCustomerDTO.getBillingCategory());
        assertEquals(request.getBillingType(), kycCustomerDTO.getBillingType());
    }

}
