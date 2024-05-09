package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.DataChangeException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.CustomerRepository;
import com.bayu.billingservice.repository.InvestmentManagementRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.service.CustomerService;
import com.bayu.billingservice.service.InvestmentManagementService;
import com.bayu.billingservice.util.EnumValidator;
import com.bayu.billingservice.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final BillingDataChangeService dataChangeService;
    private final InvestmentManagementService investmentManagementService;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    private static final String CODE_NOT_FOUND = "Investment Management not found with code: ";

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return customerRepository.existsByCustomerCode(code);
    }

    @Override
    public CreateCustomerListResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single billing customer with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public List<CustomerDTO> getAll() {
        return List.of();
    }

    @Override
    public List<CustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        return List.of();
    }

    @Override
    public String deleteAll() {
        return "";
    }

    @Override
    public CreateCustomerListResponse createMultipleData(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public CreateCustomerListResponse createMultipleApprove(CreateCustomerListRequest request) {
        return null;
    }

    @Override
    public UpdateCustomerListResponse updateSingleData(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateCustomerListResponse updateMultipleData(UpdateCustomerListRequest updateCustomerListRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateCustomerListResponse updateMultipleApprove(UpdateCustomerListRequest updateCustomerListRequest) {
        return null;
    }

    @Override
    public DeleteCustomerListResponse deleteSingleData(DeleteCustomerRequest deleteCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public DeleteCustomerListResponse deleteMultipleApprove(DeleteCustomerListRequest deleteCustomerListRequest) {
        return null;
    }

    private void validateDataChangeIds(List<CustomerDTO> customerDTOList) {
        List<Long> idDataChangeList = customerDTOList.stream()
                .map(CustomerDTO::getDataChangeId)
                .toList();

        if (!dataChangeService.areAllIdsExistInDatabase(idDataChangeList)) {
            log.info("Data Change id not found");
            throw new DataChangeException("Data Change id not found");
        }
    }

    private void validateBillingEnums(CustomerDTO customerDTO, List<String> validationErrors) {
        if (!EnumValidator.validateEnumBillingCategory(customerDTO.getBillingCategory())) {
            validationErrors.add("Billing Category enum not found with value: " + customerDTO.getBillingCategory());
        }
        if (!EnumValidator.validateEnumBillingType(customerDTO.getBillingType())) {
            validationErrors.add("Billing Type enum not found with value: " + customerDTO.getBillingType());
        }
        if (!EnumValidator.validateEnumBillingTemplate(customerDTO.getBillingTemplate())) {
            validationErrors.add("Billing Template enum not found with value '" + customerDTO.getBillingTemplate());
        }
    }


}
