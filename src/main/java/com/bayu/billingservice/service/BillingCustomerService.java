package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.BillingCustomerDTO;

import java.util.List;

public interface BillingCustomerService {

    BillingCustomerDTO create(CreateKycRequest request);

    List<BillingCustomerDTO> getAll();

    List<BillingCustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType);

    String deleteAll();
}
