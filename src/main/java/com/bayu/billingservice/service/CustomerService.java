package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;

import java.util.List;

public interface CustomerService {

    boolean isCodeAlreadyExists(String code);

    List<CustomerDTO> getAll();

    List<CustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType);

    String deleteAll();

    CreateCustomerListResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    CreateCustomerListResponse createList(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    CreateCustomerListResponse createApprove(CreateCustomerListRequest request);

    UpdateCustomerListResponse updateList(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);
}
