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

    CreateCustomerListResponse createMultipleData(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    CreateCustomerListResponse createMultipleApprove(CreateCustomerListRequest request);

    UpdateCustomerListResponse updateSingleData(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    UpdateCustomerListResponse updateMultipleData(UpdateCustomerListRequest updateCustomerListRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateCustomerListResponse updateMultipleApprove(UpdateCustomerListRequest updateCustomerListRequest);

    DeleteCustomerListResponse deleteSingleData(DeleteCustomerRequest deleteCustomerRequest, BillingDataChangeDTO dataChangeDTO);

    DeleteCustomerListResponse deleteMultipleApprove(DeleteCustomerListRequest deleteCustomerListRequest);
}
