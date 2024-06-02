package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.Customer;

import java.util.List;

public interface CustomerService {

    boolean isCodeAlreadyExists(String code, String subCode);

    List<CustomerDTO> getAll();

    Customer getByCustomerCodeAndSubCodeAndBillingCategoryAndBillingType(String customerCode, String subCode, String billingCategory, String billingType);

    List<Customer> getAllByBillingCategoryAndBillingType(String billingCategory, String billingType);

    String deleteAll();

    CustomerResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse createMultipleData(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse createSingleApprove(CustomerApproveRequest request, String clientIP);

    CustomerResponse updateSingleData(UpdateCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse updateMultipleData(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse updateSingleApprove(CustomerApproveRequest request, String clientIP);

    CustomerResponse deleteSingleData(DeleteCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse deleteSingleApprove(CustomerApproveRequest request, String clientIP);

}
