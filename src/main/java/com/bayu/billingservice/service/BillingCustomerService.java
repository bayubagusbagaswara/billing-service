package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;

import java.util.List;

public interface BillingCustomerService {

    KycCustomerDTO create(CreateKycRequest request);

    List<KycCustomerDTO> getAll();

    List<KycCustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType);

    String deleteAll();
}
