package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.model.KycCustomer;

public interface KycService {

    // create data Kyc
    KycCustomer create(CreateKycRequest request);

    // get data Kyc by category billing and type billing
    KycCustomer getByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
