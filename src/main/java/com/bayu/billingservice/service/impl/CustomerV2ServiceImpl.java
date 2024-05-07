package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.exception.DataChangeException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.repository.CustomerRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.service.CustomerV2Service;
import com.bayu.billingservice.service.InvestmentManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
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
        return customerRepository.existsByCustomerCode(code);
    }

    @Override
    public List<CustomerDTO> getAll() {
        return mapToDTOList(customerRepository.findAll());
    }

    @Override
    public String deleteAll() {
        customerRepository.deleteAll();
        return "Successfully delete all billing customer;";
    }

    @Override
    public CreateCustomerListResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data billing customer with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        CustomerDTO customerDTO = CustomerDTO.builder()
                .customerCode(request.getCustomerCode())
                .customerName(request.getCustomerName())
                .investmentManagementCode(request.getInvestmentManagementCode())
                .billingCategory(request.getBillingCategory())
                .billingType(request.getBillingType())
                .billingTemplate(request.getBillingTemplate())
                .sellingAgentCode(request.getSellingAgentCode())
                .currency(request.getCurrency())
                .build();

        try {
            List<String> validationErrors = new ArrayList<>();
            validationCustomerCodeAlreadyExists(customerDTO.getCustomerCode(), validationErrors);
            // dia cuma ngecek customer code already exist atau gak
            // jika sudah tersedia, maka error dimasukkan kedalam validationErrors
            // tidak perlu validasi data dan cek investment code (dilakukan saat approve)
            // Check if InvestmentManagement exists
            if (validationErrors.isEmpty()) {
                // Prepare dataChangeDTO
                dataChangeDTO.setInputId(request.getInputId());
                dataChangeDTO.setInputIPAddress(request.getInputIPAddress());

                // Use ObjectMapper to serialize customerDTO to JSON string
                String jsonDataAfter = objectMapper.writeValueAsString(customerDTO);
                dataChangeDTO.setJsonDataAfter(jsonDataAfter);

                // Create change action using dataChangeService
                dataChangeService.createChangeActionADD(dataChangeDTO, Customer.class);

                // Increment totalDataSuccess
                totalDataSuccess++;
            } else {
                // Add error message with customer code to errorMessageList
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);

                // Increment totalDataFailed
                totalDataFailed++;
            }
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            // Handle general exception
            handleGeneralError(e);
        }
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
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
    public UpdateCustomerListResponse updateMultipleData(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public UpdateCustomerListResponse updateMultipleApprove(UpdateCustomerListRequest request) {
        return null;
    }

    @Override
    public DeleteCustomerListResponse deleteSingleData(DeleteCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public DeleteCustomerListResponse deleteMultipleApprove(DeleteCustomerListRequest request) {
        return null;
    }

    private static CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .customerName(customer.getCustomerName())
                .investmentManagementCode(customer.getInvestmentManagementCode())
                .investmentManagementName(customer.getInvestmentManagementName())
                .accountName(customer.getAccountName())
                .accountNumber(customer.getAccountNumber())
                .accountBank(customer.getAccountBank())
                .kseiSafeCode(customer.getKseiSafeCode())
                .customerMinimumFee(customer.getCustomerMinimumFee())
                .customerSafekeepingFee(customer.getCustomerSafekeepingFee())
                .billingCategory(customer.getBillingCategory())
                .billingType(customer.getBillingType())
                .billingTemplate(customer.getBillingTemplate())
                .currency(customer.getCurrency())
                .sellingAgentCode(customer.getSellingAgent())
                .build();
    }

    private static List<CustomerDTO> mapToDTOList(List<Customer> customerList) {
        return customerList.stream()
                .map(CustomerV2ServiceImpl::mapToDTO)
                .toList();
    }

    private void handleDataChangeException(DataChangeException e) {
        log.error("Data Change exception occurred: {}", e.getMessage());
        throw new DataChangeException("Data Change exception occurred: " + e.getMessage());
    }

    private void handleJsonProcessingException(JsonProcessingException e) {
        log.error("Error processing JSON during data change logging: {}", e.getMessage(), e);
        throw new DataChangeException("Error processing JSON during data change logging", e);
    }

    private void handleGeneralError(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        throw new DataChangeException("An unexpected error occurred: " + e.getMessage());
    }

    public Errors validateCustomerUsingValidator(CustomerDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "customerDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void validationCustomerCodeAlreadyExists(String customerCode, List<String> validationErrors) {
        if (isCodeAlreadyExists(customerCode)) {
            validationErrors.add("Billing Customer already taken with code: " + customerCode);
        }
    }

}
