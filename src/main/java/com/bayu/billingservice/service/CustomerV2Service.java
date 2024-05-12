package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;

import java.util.List;

public interface CustomerV2Service {

    CustomerDTO testCreate(CustomerDTO dto);

    List<CustomerDTO> getAllTest();

    boolean isCodeAlreadyExists(String code);

    List<CustomerDTO> getAll();

    String deleteAll();

    CreateCustomerListResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    CreateCustomerListResponse createMultipleData(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    CreateCustomerListResponse createMultipleApprove(CreateCustomerListRequest request);

    UpdateCustomerListResponse updateSingleData(UpdateCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    UpdateCustomerListResponse updateMultipleData(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    UpdateCustomerListResponse updateMultipleApprove(UpdateCustomerListRequest request);

    DeleteCustomerListResponse deleteSingleData(DeleteCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    DeleteCustomerListResponse deleteMultipleApprove(DeleteCustomerListRequest request);

}
