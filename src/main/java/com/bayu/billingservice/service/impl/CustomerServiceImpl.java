package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.customer.CreateCustomerListResponse;
import com.bayu.billingservice.dto.customer.CreateCustomerRequest;
import com.bayu.billingservice.dto.customer.CustomerDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.DataChangeException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.repository.CustomerRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.service.CustomerService;
import com.bayu.billingservice.service.InvestmentManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
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

        try {
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

            List<String> errorMessages = new ArrayList<>();
            Errors errors = validateBillingCustomerUsingValidator(customerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));
            }

            validationCustomerCodeAlreadyExists(customerDTO, errorMessages);
            
            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getInvestmentManagementCode());
            customerDTO.setInvestmentManagementName(investmentManagementDTO.getName());

            dataChangeDTO.setInputId(request.getInputId());
            dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
            dataChangeDTO.setJsonDataAfter(objectMapper.writeValueAsString(customerDTO));

            if (errorMessages.isEmpty()) {
                dataChangeService.createChangeActionADD(dataChangeDTO, Customer.class);
                totalDataSuccess++;
            } else {
                totalDataFailed++;
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), errorMessages);
                errorMessageList.add(errorMessageDTO);
            }
            return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (DataNotFoundException e) {
          totalDataFailed = getTotalDataFailed(e, errorMessageList, totalDataFailed);
          return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private void validationCustomerCodeAlreadyExists(CustomerDTO customerDTO, List<String> errorMessages) {
        if (isCodeAlreadyExists(customerDTO.getCustomerCode())) {
            errorMessages.add("Billing Customer already taken with code: " + customerDTO.getCustomerCode());
        }
    }

    @Override
    public List<CustomerDTO> getAll() {
        return mapToDTOList(customerRepository.findAll());
    }

    @Override
    public List<CustomerDTO> getByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        List<Customer> customerList = customerRepository.findByBillingCategoryAndBillingType(billingCategory, billingType);
        return mapToDTOList(customerList);
    }

    @Override
    public String deleteAll() {
        try {
            customerRepository.deleteAll();
            return "Successfully deleted all Kyc Customer";
        } catch (Exception e) {
            log.error("Error when delete all Kyc Customer : {}", e.getMessage(), e);
            throw new ConnectionDatabaseException("Error when delete all Kyc Customer");
        }
    }

    private static CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
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
                .build();
    }

    private static List<CustomerDTO> mapToDTOList(List<Customer> customerList) {
        return customerList.stream()
                .map(CustomerServiceImpl::mapToDTO)
                .toList();
    }

    public Errors validateBillingCustomerUsingValidator(CustomerDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "customerDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private static int getTotalDataFailed(DataNotFoundException e, List<ErrorMessageDTO> errorMessageList, int totalDataFailed) {
        log.error("Billing Customer not found: {}", e.getMessage(), e);
        errorMessageList.add(new ErrorMessageDTO(null, Collections.singletonList(e.getMessage())));
        totalDataFailed++;
        return totalDataFailed;
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

    private void validateDataChangeIds(List<CustomerDTO> customerDTOList) {
        List<Long> idDataChangeList = customerDTOList.stream()
                .map(CustomerDTO::getDataChangeId)
                .toList();

        if (!dataChangeService.areAllIdsExistInDatabase(idDataChangeList)) {
            log.info("Data Change id not found");
            throw new DataChangeException("Data Change id not found");
        }
    }
}
