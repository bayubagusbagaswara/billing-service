package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;

public interface KycCustomerService {

    KycCustomerDTO create(CreateKycRequest request);

    KycCustomerDTO getByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
