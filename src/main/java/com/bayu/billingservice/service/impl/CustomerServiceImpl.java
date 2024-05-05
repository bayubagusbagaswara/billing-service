package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.customer.*;
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
import com.bayu.billingservice.util.EnumValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Override
    public CreateCustomerListResponse createList(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create billing customer list with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>(); // List to collect all error messages

        for (CustomerDTO customerDTO : request.getCustomerDTOList()) {
            try {
                List<String> validationErrors = new ArrayList<>();

                Errors errors = validateBillingCustomerUsingValidator(customerDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(objectError -> validationErrors.add(objectError.getDefaultMessage()));
                }

                validateBillingEnums(customerDTO, validationErrors);

                validationCustomerCodeAlreadyExists(customerDTO, validationErrors);

                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getInvestmentManagementCode());
                customerDTO.setInvestmentManagementName(investmentManagementDTO.getName());

                dataChangeDTO.setInputId(request.getInputId());
                dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
                dataChangeDTO.setJsonDataAfter(objectMapper.writeValueAsString(customerDTO));

                if (validationErrors.isEmpty()) {
                    dataChangeService.createChangeActionADD(dataChangeDTO, Customer.class);
                    totalDataSuccess++;
                } else {
                    totalDataFailed++;
                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), validationErrors);
                    errorMessageList.add(errorMessageDTO);
                }
            } catch (DataNotFoundException e) {
                List<String> errorMessages = new ArrayList<>();
                errorMessages.add(e.getMessage());
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), errorMessages);
                errorMessageList.add(errorMessageDTO);
                totalDataFailed++;
            } catch (JsonProcessingException e) {
                handleJsonProcessingException(e);
            } catch (Exception e) {
                handleGeneralError(e);
            }
        }
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public CreateCustomerListResponse createApprove(CreateCustomerListRequest request) {
        log.info("Approve for create billing customer with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        try {
            validateDataChangeIds(request.getCustomerDTOList());
            for (CustomerDTO customerDTO : request.getCustomerDTOList()) {
                try {
                    List<String> validationErrors = new ArrayList<>();
                    Errors errors = validateBillingCustomerUsingValidator(customerDTO);
                    if (errors.hasErrors()) {
                        errors.getAllErrors().forEach(objectError -> validationErrors.add(objectError.getDefaultMessage()));
                    }

                    validateBillingEnums(customerDTO, validationErrors);

                    validationCustomerCodeAlreadyExists(customerDTO, validationErrors);

                    InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getInvestmentManagementCode());
                    customerDTO.setInvestmentManagementName(investmentManagementDTO.getName());

                    if (!validationErrors.isEmpty()) {
                        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                                .id(customerDTO.getDataChangeId())
                                .approveId(request.getApproveId())
                                .approveIPAddress(request.getApproveIPAddress())
                                .jsonDataAfter(objectMapper.writeValueAsString(customerDTO))
                                .build();
                        dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                        totalDataFailed++;
                    } else {
                        Customer customer = createCustomer(customerDTO);
                        customerRepository.save(customer);

                        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                                .id(customerDTO.getDataChangeId())
                                .approveId(request.getApproveId())
                                .approveIPAddress(request.getApproveIPAddress())
                                .entityId(customer.getId().toString())
                                .jsonDataAfter(objectMapper.writeValueAsString(customer))
                                .description("Successfully approve data create billing customer data")
                                .build();
                        dataChangeService.approvalStatusIsApproved(dataChangeDTO);

                        totalDataSuccess++;
                    }
                } catch (DataNotFoundException e) {
                    handleDataNotFoundError(customerDTO, errorMessageList, e);
                    totalDataFailed++;
                } catch (JsonProcessingException e) {
                    handleJsonProcessingException(e);
                } catch (Exception e) {
                    handleGeneralError(e);
                }
            }
        } catch (DataChangeException e) {
            handleDataChangeException(e);
        }
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public UpdateCustomerListResponse updateList(UpdateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update list billing customer with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        // kita buat mirip seperti create list
        return null;
    }

    private Customer createCustomer(CustomerDTO customerDTO) {
        return Customer.builder()
                .customerCode(customerDTO.getCustomerCode())
                .customerName(customerDTO.getCustomerName())
                .investmentManagementCode(customerDTO.getInvestmentManagementCode())
                .investmentManagementName(customerDTO.getInvestmentManagementName())
                .build();
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

    private void handleDataNotFoundError(CustomerDTO customerDTO, List<ErrorMessageDTO> errorMessageList, DataNotFoundException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(customerDTO.getCustomerCode(), errorMessages));
    }

}
