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
    private final InvestmentManagementRepository investmentManagementRepository;

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

            List<String> validationErrors = new ArrayList<>();
            Errors errors = validateBillingCustomerUsingValidator(customerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(objectError -> validationErrors.add(objectError.getDefaultMessage()));
            }

            InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(customerDTO.getInvestmentManagementCode()).orElse(null);
            if (investmentManagement == null) {
                validationErrors.add("Investment Management not found with code: " + customerDTO.getInvestmentManagementCode());
            }
            customerDTO.setInvestmentManagementName(investmentManagement.getName());

            if (validationErrors.isEmpty()) {
                dataChangeDTO.setInputId(request.getInputId());
                dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
                dataChangeDTO.setJsonDataAfter(objectMapper.writeValueAsString(customerDTO));
                dataChangeService.createChangeActionADD(dataChangeDTO, Customer.class);
                totalDataSuccess++;
            } else {
                totalDataFailed++;
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), validationErrors);
                errorMessageList.add(errorMessageDTO);
            }
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

                    InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(customerDTO.getInvestmentManagementCode())
                            .orElseThrow(() -> new DataNotFoundException("Investment Management not found with code: " + customerDTO.getInvestmentManagementCode()));

                    customerDTO.setInvestmentManagementName(investmentManagement.getName());

                    if (!validationErrors.isEmpty()) {
                        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                                .id(customerDTO.getDataChangeId())
                                .approveId(request.getApproveId())
                                .approveIPAddress(request.getApproveIPAddress())
                                .jsonDataAfter(objectMapper.writeValueAsString(customerDTO))
                                .description(StringUtil.joinStrings(validationErrors))
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
                    log.error("Investment Management not found with code: {}", customerDTO != null ? customerDTO.getInvestmentManagementCode() : "unknown", e);

                    List<String> validationErrors = new ArrayList<>();
                    validationErrors.add("Investment Management not found with code: " + (customerDTO != null ? customerDTO.getInvestmentManagementCode() : "unknown"));

                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : "unknown", validationErrors);
                    errorMessageList.add(errorMessageDTO);

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

    public CreateCustomerListResponse createSingleDataNew(@Valid CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();
        CustomerDTO customerDTO = CustomerDTO.builder()
                .customerCode(request.getCustomerCode())
                .customerName(request.getCustomerName())
                .investmentManagementCode(request.getInvestmentManagementCode())
                .billingCategory(request.getBillingCategory())
                .billingType(request.getBillingType())
                .billingTemplate(request.getBillingTemplate())
                .sellingAgentCode(request.getSellingAgentCode())
                .currency(request.getCurrency())
                .build(); // Initialize customerDTO outside try block

        try {
            // Validate CustomerDTO using Spring Validator
            Errors errors = validateBillingCustomerUsingValidator(customerDTO);

            List<String> validationErrors = new ArrayList<>();
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(objectError -> validationErrors.add(objectError.getDefaultMessage()));
            }

            // Check if InvestmentManagement exists
            InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(customerDTO.getInvestmentManagementCode())
                    .orElseThrow(() -> new IllegalArgumentException("Investment Management not found with code: " + customerDTO.getInvestmentManagementCode()));

            // Set investment management name
            customerDTO.setInvestmentManagementName(investmentManagement.getName());

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
                errorMessageList.add(errorMessageDTO);

                // Increment totalDataFailed
                totalDataFailed++;
            }

        } catch (IllegalArgumentException e) {
            // Log error using logger
            log.error("Investment Management not found with code: {}", customerDTO != null ? customerDTO.getInvestmentManagementCode() : "unknown", e);

            // Handle IllegalArgumentException
            List<String> validationErrors = new ArrayList<>();
            validationErrors.add("Investment Management not found with code: " + (customerDTO != null ? customerDTO.getInvestmentManagementCode() : "unknown"));

            // Add error message with customer code to errorMessageList
            ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : "unknown", validationErrors);
            errorMessageList.add(errorMessageDTO);

            // Increment totalDataFailed
            totalDataFailed++;
        } catch (Exception e) {
            // Log error using logger
            log.error("An unexpected error occurred", e);

            // Handle general exception
            List<String> validationErrors = new ArrayList<>();
            validationErrors.add("An unexpected error occurred: " + e.getMessage());

            // Add error message with customer code to errorMessageList
            ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : "unknown", validationErrors);
            errorMessageList.add(errorMessageDTO);

            // Increment totalDataFailed
            totalDataFailed++;
        }

        // Return CreateCustomerListResponse with totalDataSuccess and totalDataFailed
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    public CreateCustomerListResponse createMultipleData(List<CreateCustomerRequest> requests, List<BillingDataChangeDTO> dataChangeDTOs) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            CreateCustomerRequest request = requests.get(i);
            BillingDataChangeDTO dataChangeDTO = dataChangeDTOs.get(i);

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
                // Validate CustomerDTO using Spring Validator
                Errors errors = validateBillingCustomerUsingValidator(customerDTO);

                List<String> validationErrors = new ArrayList<>();
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(objectError -> validationErrors.add(objectError.getDefaultMessage()));
                }

                // Check if InvestmentManagement exists
                InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(customerDTO.getInvestmentManagementCode())
                        .orElseThrow(() -> new IllegalArgumentException("Investment Management not found with code: " + customerDTO.getInvestmentManagementCode()));

                // Set investment management name
                customerDTO.setInvestmentManagementName(investmentManagement.getName());

                if (validationErrors.isEmpty()) {
                    // Prepare dataChangeDTO
                    dataChangeDTO.setInputId(request.getInputId());
                    dataChangeDTO.setInputIPAddress(request.getInputIPAddress());

                    // Perform data change action using dataChangeService
                    dataChangeService.createChangeActionADD(dataChangeDTO, Customer.class);

                    // Increment totalDataSuccess
                    totalDataSuccess++;
                } else {
                    // Add error message with customer code to errorMessageList
                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), validationErrors);
                    errorMessageList.add(errorMessageDTO);

                    // Increment totalDataFailed
                    totalDataFailed++;
                }

            } catch (IllegalArgumentException e) {
                // Log error using logger
                log.error("Investment Management not found with code: {}", customerDTO != null ? customerDTO.getInvestmentManagementCode() : "unknown", e);

                // Handle IllegalArgumentException
                List<String> validationErrors = new ArrayList<>();
                validationErrors.add("Investment Management not found with code: " + (customerDTO != null ? customerDTO.getInvestmentManagementCode() : "unknown"));

                // Add error message with customer code to errorMessageList
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : "unknown", validationErrors);
                errorMessageList.add(errorMessageDTO);

                // Increment totalDataFailed
                totalDataFailed++;
            } catch (Exception e) {
                // Log error using logger
                log.error("An unexpected error occurred", e);

                // Handle general exception
                List<String> validationErrors = new ArrayList<>();
                validationErrors.add("An unexpected error occurred: " + e.getMessage());

                // Add error message with customer code to errorMessageList
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : "unknown", validationErrors);
                errorMessageList.add(errorMessageDTO);

                // Increment totalDataFailed
                totalDataFailed++;
            }
        }

        // Create and return DataProcessingResponse
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

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

                    // Validate CustomerDTO using validator
                    Errors errors = validateBillingCustomerUsingValidator(customerDTO);
                    if (errors.hasErrors()) {
                        errors.getAllErrors().forEach(objectError -> validationErrors.add(objectError.getDefaultMessage()));
                    }

                    // Additional custom validations
                    validateBillingEnums(customerDTO, validationErrors);
                    validateCustomerCodeAlreadyExists(customerDTO, validationErrors);

                    // Retrieve and set InvestmentManagement
                    InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(customerDTO.getInvestmentManagementCode())
                            .orElseThrow(() -> new DataNotFoundException("Investment Management not found with code: " + customerDTO.getInvestmentManagementCode()));
                    customerDTO.setInvestmentManagementName(investmentManagement.getName());

                    if (!validationErrors.isEmpty()) {
                        // Data change approval status is rejected
                        BillingDataChangeDTO dataChangeDTO = createRejectedDataChangeDTO(customerDTO, request, validationErrors);
                        dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                        errorMessageList.add(new ErrorMessageDTO(customerDTO.getCustomerCode(), validationErrors));
                        totalDataFailed++;
                    } else {
                        // Create and save Customer entity
                        Customer customer = createCustomer(customerDTO);
                        customerRepository.save(customer);

                        // Data change approval status is approved
                        BillingDataChangeDTO dataChangeDTO = createApprovedDataChangeDTO(customerDTO, request, customer);
                        dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                        totalDataSuccess++;
                    }
                } catch (DataNotFoundException e) {
                    handleDataNotFoundException(customerDTO, e, errorMessageList);
                    totalDataFailed++;
                } catch (JsonProcessingException e) {
                    handleJsonProcessingException(e);
                    totalDataFailed++;
                } catch (Exception e) {
                    handleGeneralError(e);
                    totalDataFailed++;
                }
            }
        } catch (DataChangeException e) {
            handleDataChangeException(e);
            // If overall data change exception, all data might be considered failed
            totalDataFailed = request.getCustomerDTOList().size();
        }

        // Return response with total success, total failed, and error messages
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private BillingDataChangeDTO createRejectedDataChangeDTO(CustomerDTO customerDTO, CreateCustomerListRequest request, List<String> validationErrors) throws JsonProcessingException {
        return BillingDataChangeDTO.builder()
                .id(customerDTO.getDataChangeId())
                .approveId(request.getApproveId())
                .approveIPAddress(request.getApproveIPAddress())
                .jsonDataAfter(objectMapper.writeValueAsString(customerDTO))
                .description(StringUtil.joinStrings(validationErrors))
                .build();
    }

    private void handleDataNotFoundException(CustomerDTO customerDTO, DataNotFoundException e, List<ErrorMessageDTO> errorMessageList) {
        log.error("Investment Management not found with code: {}", customerDTO != null ? customerDTO.getInvestmentManagementCode() : "unknown", e);

        List<String> validationErrors = new ArrayList<>();
        validationErrors.add("Investment Management not found with code: " + (customerDTO != null ? customerDTO.getInvestmentManagementCode() : "unknown"));

        errorMessageList.add(new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : "unknown", validationErrors));
    }
}
