package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.model.KycCustomer;

public interface KycCustomerService {

    KycCustomer create(CreateKycRequest request);

    KycCustomer getByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
