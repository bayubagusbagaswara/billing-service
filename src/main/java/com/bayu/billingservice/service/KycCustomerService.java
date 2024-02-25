package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;

import java.util.List;

public interface KycCustomerService {

    KycCustomerDTO create(CreateKycRequest request);

    List<KycCustomerDTO> getAll();

    KycCustomerDTO getByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
