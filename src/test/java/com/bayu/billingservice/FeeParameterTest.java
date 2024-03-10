package com.bayu.billingservice;

import com.bayu.billingservice.dto.feeparameter.CreateFeeParameterRequest;
import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;
import com.bayu.billingservice.repository.FeeParameterRepository;
import com.bayu.billingservice.service.FeeParameterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FeeParameterTest {

    @Autowired
    FeeParameterService feeParameterService;

    @Autowired
    FeeParameterRepository feeParameterRepository;

    @BeforeEach
    void cleanUpAndInsertData()  {
        feeParameterRepository.deleteAll();
    }

    @Test
    void insertData() {
        // Define test data
        String[][] testData = {
                {"TRANSACTION_HANDLING_IDR", "Transaction Handling Fee IDR", "50000"},
                {"KSEI", "KSEI Fee", "22200"},
                {"BI-SSSS", "BI-SSSS Fee", "23000"},
                {"VAT", "PPN (VAT) Fee", "0.11"},
                {"ADMINISTRATION_SET_UP", "Administration Set Up Fee USD", "5000"},
                {"SIGNING_REPRESENTATION", "Signing Representation Fee USD", "2000"},
                {"SECURITY_AGENT", "Security Agent Fee USD", "10000"},
                {"TRANSACTION_HANDLING_USD", "Transaction Handling Fee USD", "100"},
                {"OTHER", "Other Fee USD", "5000"}
        };

        // Run test for each set of data
        for (String[] data : testData) {
            String name = data[0];
            String description = data[1];
            String value = data[2];

            CreateFeeParameterRequest request = CreateFeeParameterRequest.builder()
                    .name(name)
                    .description(description)
                    .value(value)
                    .build();

            FeeParameterDTO feeParameterDTO = feeParameterService.create(request);
            assertNotNull(feeParameterDTO.getId());
            assertEquals(request.getName(), feeParameterDTO.getName());
        }
    }

    //    @ParameterizedTest
//    @CsvSource({
//            "TRANSACTION_HANDLING, Transaction Handling Fee, 5000",
//            "KSEI, KSEI Fee, 22200",
//            "BIS4, BI-SSSS Fee, 23000",
//            "VAT, PPN (VAT) Fee, 0.11"
//    })
//    void createExchangeRate(String name, String description, String value) {
//        CreateFeeParameterRequest request = CreateFeeParameterRequest.builder()
//                .name(name)
//                .description(description)
//                .value(value)
//                .build();
//
//        FeeParameterDTO feeParameterDTO = feeParameterService.create(request);
//        assertNotNull(feeParameterDTO.getId());
//        assertEquals(feeParameterDTO.getName(), request.getName());
//    }

}
