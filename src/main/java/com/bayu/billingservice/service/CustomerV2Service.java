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

    CustomerResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse createMultipleData(CustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse createSingleApprove(CustomerApproveRequest request);

    CustomerResponse updateSingleData(UpdateCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse updateMultipleData(CustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse updateSingleApprove(CustomerApproveRequest request);

    CustomerResponse deleteSingleData(DeleteCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    CustomerResponse deleteSingleApprove(CustomerApproveRequest request);

}
