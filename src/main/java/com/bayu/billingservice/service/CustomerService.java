package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.customer.CreateCustomerListResponse;
import com.bayu.billingservice.dto.customer.CreateCustomerRequest;
import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;

import java.util.List;

public interface CustomerService {

    boolean isCodeAlreadyExists(String code);

    List<CustomerDTO> getAll();

    List<CustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType);

    String deleteAll();

    CreateCustomerListResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO);
}
