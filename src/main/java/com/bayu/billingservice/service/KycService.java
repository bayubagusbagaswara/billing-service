package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.model.Kyc;

public interface KycService {

    // create data Kyc
    Kyc create(CreateKycRequest request);

    // get data Kyc by category billing and type billing
    Kyc getByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
