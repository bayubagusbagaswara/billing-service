package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;

import java.util.List;

public interface CustomerV2Service {

    boolean isCodeAlreadyExists(String code);

    List<CustomerDTO> getAll();

    String deleteAll();

    CreateCustomerListResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    CreateCustomerListResponse createListData(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    CreateCustomerListResponse createListApprove(CreateCustomerListRequest request);

    UpdateCustomerListResponse updateSingleData(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    UpdateCustomerListResponse updateListData(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO);

    UpdateCustomerListResponse updateListApprove(UpdateCustomerListRequest request);

    DeleteCustomerListResponse deleteSingleData(DeleteCustomerRequest request, BillingDataChangeDTO dataChangeDTO);

    DeleteCustomerListResponse deleteListApprove(DeleteCustomerListRequest request);

}
