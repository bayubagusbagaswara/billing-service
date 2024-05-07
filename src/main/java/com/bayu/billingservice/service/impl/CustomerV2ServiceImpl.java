package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.repository.CustomerRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.service.CustomerV2Service;
import com.bayu.billingservice.service.InvestmentManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerV2ServiceImpl implements CustomerV2Service {

    private final CustomerRepository customerRepository;
    private final BillingDataChangeService dataChangeService;
    private final InvestmentManagementService investmentManagementService;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    private static final String ID_NOT_FOUND = "Billing Customer not found with id: ";
    private static final String CODE_NOT_FOUND = "Billing Customer not found with code: ";

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return false;
    }

    @Override
    public List<CustomerDTO> getAll() {
        return List.of();
    }

    @Override
    public String deleteAll() {
        return "";
    }

    @Override
    public CreateCustomerListResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public CreateCustomerListResponse createListData(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public CreateCustomerListResponse createListApprove(CreateCustomerListRequest request) {
        return null;
    }

    @Override
    public UpdateCustomerListResponse updateSingleData(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateCustomerListResponse updateListData(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateCustomerListResponse updateListApprove(UpdateCustomerListRequest request) {
        return null;
    }

    @Override
    public DeleteCustomerListResponse deleteSingleData(DeleteCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public DeleteCustomerListResponse deleteListApprove(DeleteCustomerListRequest request) {
        return null;
    }
}
