package com.bayu.billingservice;

import com.bayu.billingservice.dto.kyc.BillingCustomerDTO;
import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.model.enumerator.BillingCategory;
import com.bayu.billingservice.model.enumerator.BillingTemplate;
import com.bayu.billingservice.model.enumerator.BillingType;
import com.bayu.billingservice.service.BillingCustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BillingCustomerFundTest {

    @Autowired
    BillingCustomerService billingCustomerService;

    @Test
    void createCustomerFund() {
        CreateKycRequest request = CreateKycRequest.builder()
                .aid("24SDOU")
                .customerMinimumFee("")
                .customerSafekeepingFee("")
                .investmentManagementName("PT Setiabudi Investment Management")
                .investmentManagementAddressBuilding("Setiabudi Atrium Lt 5 Suites 501A")
                .investmentManagementAddressStreet("Jl HR Rasuna Said Kav 62")
                .investmentManagementAddressCity("Kuningan Timur")
                .investmentManagementAddressProvince("Jakarta Selatan")
                .accountName("Reksa Dana Setiabudi Dana Obligasi Unggulan")
                .accountNumber("3607809104")
                .accountBank("PT Bank Danamon Indonesia, Tbk")
                .kseiSafeCode("")
                .billingCategory(BillingCategory.FUND.getValue())
                .billingType(BillingType.TYPE_1.getValue())
                .billingTemplate(BillingTemplate.FUND_TEMPLATE.getValue())
                .build();

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);
        assertNotNull(billingCustomerDTO.getId());
    }
}
