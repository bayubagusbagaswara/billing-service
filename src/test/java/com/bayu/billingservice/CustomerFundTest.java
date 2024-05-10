package com.bayu.billingservice;

import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.dto.customer.CreateCustomerRequest;
import com.bayu.billingservice.model.enumerator.BillingCategory;
import com.bayu.billingservice.model.enumerator.BillingTemplate;
import com.bayu.billingservice.model.enumerator.BillingType;
import com.bayu.billingservice.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerFundTest {

    @Autowired
    CustomerService customerService;

    @Test
    void createCustomerFund() {
        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .customerCode("24SDOU")
                .customerMinimumFee(BigDecimal.ZERO)
                .customerSafekeepingFee(BigDecimal.ZERO)
                .accountName("Reksa Dana Setiabudi Dana Obligasi Unggulan")
                .accountNumber("3607809104")
                .accountBank("PT Bank Danamon Indonesia, Tbk")
                .kseiSafeCode("")
                .billingCategory(BillingCategory.FUND.getValue())
                .billingType(BillingType.TYPE_1.getValue())
                .billingTemplate(BillingTemplate.FUND_TEMPLATE_1.getValue())
                .build();

    }
}
