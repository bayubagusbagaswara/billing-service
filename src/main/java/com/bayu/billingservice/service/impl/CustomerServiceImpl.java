package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.exception.InvalidInputException;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.repository.CustomerRepository;
import com.bayu.billingservice.service.*;
import com.bayu.billingservice.mapper.CustomerMapper;
import com.bayu.billingservice.util.BeanUtil;
import com.bayu.billingservice.util.EnumValidator;
import com.bayu.billingservice.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private static final String ID_NOT_FOUND = "Billing Customer not found with id: ";
    private static final String CODE_NOT_FOUND = "Billing Customer not found with code: ";
    private static final String SUB_CODE_NOT_FOUND = " and sub code: ";
    private static final String UNKNOWN = "unknown";
    private static final String INVALID_VALUE = "Invalid value for isGL. Value must be 'TRUE' or 'FALSE'.";

    private final CustomerRepository customerRepository;
    private final DataChangeService dataChangeService;
    private final InvestmentManagementService investmentManagementService;
    private final SellingAgentService sellingAgentService;
    private final BillingTemplateService billingTemplateService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final CustomerMapper customerMapper;

    @Override
    public boolean isCodeAlreadyExists(String code, String subCode) {
        return customerRepository.existsCustomerByCustomerCodeAndSubCode(code, subCode);
    }

    @Override
    public List<CustomerDTO> getAll() {
        List<Customer> all = customerRepository.findAll();
        return customerMapper.mapToDTOList(all);
    }

    @Override
    public List<Customer> getAllByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        return customerRepository.findAllByBillingCategoryAndBillingType(billingCategory, billingType);
    }

    @Override
    public String deleteAll() {
        customerRepository.deleteAll();
        return "Successfully delete all billing customer;";
    }

    @Override
    public CustomerResponse createSingleData(CreateCustomerRequest createCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data billing customer with request: {}", createCustomerRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        try {
            List<String> validationErrors = new ArrayList<>();

            /* mapping data from request to dto */
            CustomerDTO customerDTO = customerMapper.mapFromCreateRequestToDto(createCustomerRequest);
            log.info("[Create Single] Map from request to dto: {}", customerDTO);

            /* validation for each column dto */
            Errors errors = validateCustomerUsingValidator(customerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* validation code and sub code already exists */
            validationCustomerCodeAndSubCodeAlreadyExists(customerDTO.getCustomerCode(), customerDTO.getSubCode(), validationErrors);

            /* validating sales agent is available or not */
            if (!StringUtils.isEmpty(customerDTO.getSellingAgent())) {
                validationSellingAgentCodeAlreadyExists(customerDTO.getSellingAgent(), validationErrors);
            }

            /* validation enum data */
            validateBillingEnums(customerDTO.getBillingCategory(),
                    customerDTO.getBillingType(),
                    customerDTO.getCurrency(),
                    validationErrors);

            /* validation value GL must be true or false */
            if (isValidIsGLValue(customerDTO.getGl())) {
                throw new InvalidInputException(INVALID_VALUE);
            }

            /* validating Cost Center Debit */
            validateGLForCostCenterDebit(Boolean.parseBoolean(customerDTO.getGl()), customerDTO.getDebitTransfer(), validationErrors);

            /* validating data billing template */
            validationBillingTemplate(customerDTO.getBillingCategory(),
                    customerDTO.getBillingType(),
                    customerDTO.getCurrency(),
                    customerDTO.getSubCode(),
                    customerDTO.getBillingTemplate(),
                    validationErrors);

            /* validating data Investment Management is available or not */
            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getMiCode());
            customerDTO.setMiCode(investmentManagementDTO.getCode());
            customerDTO.setMiName(investmentManagementDTO.getName());

            /* set data input id to data change */
            dataChangeDTO.setInputId(createCustomerRequest.getInputId());

            /* check validation errors for custom response */
            if (validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerDTO)));
                dataChangeService.createChangeActionADD(dataChangeDTO, Customer.class);
                totalDataSuccess++;
            } else {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            }
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageDTOList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public CustomerResponse createMultipleData(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create billing customer multiple data with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        /* repeat data one by one */
        for (CreateCustomerDataListRequest createCustomerDataListRequest : request.getCreateCustomerDataListRequests()) {
            try {
                /* mapping data from request to dto */
                CustomerDTO customerDTO = customerMapper.mapFromDataListToDTO(createCustomerDataListRequest);
                log.info("[Create Multiple] mapper from create request to dto: {}", customerDTO);

                /* validating for each column dto */
                List<String> validationErrors = new ArrayList<>();
                Errors errors = validateCustomerUsingValidator(customerDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
                }

                /* validating code and sub code already exists */
                validationCustomerCodeAndSubCodeAlreadyExists(customerDTO.getCustomerCode(), customerDTO.getSubCode(), validationErrors);

                /* validation selling agent is available or not */
                if (!StringUtils.isEmpty(customerDTO.getSellingAgent())) {
                    validationSellingAgentCodeAlreadyExists(customerDTO.getSellingAgent(), validationErrors);
                }

                /* validating enum data */
                validateBillingEnums(customerDTO.getBillingCategory(),
                        customerDTO.getBillingType(),
                        customerDTO.getCurrency(),
                        validationErrors);

                /* validating value GL must be true or false */
                if (isValidIsGLValue(customerDTO.getGl())) {
                    throw new InvalidInputException(INVALID_VALUE);
                }

                /* validating Cost Center Debit */
                validateGLForCostCenterDebit(Boolean.parseBoolean(customerDTO.getGl()), customerDTO.getDebitTransfer(), validationErrors);

                /* validating data billing template */
                validationBillingTemplate(customerDTO.getBillingCategory(),
                        customerDTO.getBillingType(),
                        customerDTO.getCurrency(),
                        customerDTO.getSubCode(),
                        customerDTO.getBillingTemplate(),
                        validationErrors);

                /* validating data Investment Management is available or not*/
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getMiCode());
                customerDTO.setMiCode(investmentManagementDTO.getCode());
                customerDTO.setMiName(investmentManagementDTO.getName());

                /* set data input id to data change */
                dataChangeDTO.setInputId(dataChangeDTO.getInputId());

                /* check validation errors for custom response */
                if (validationErrors.isEmpty()) {
                    dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerDTO)));
                    dataChangeService.createChangeActionADD(dataChangeDTO, Customer.class);
                    totalDataSuccess++;
                } else {
                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), validationErrors);
                    errorMessageDTOList.add(errorMessageDTO);
                    totalDataFailed++;
                }
            } catch (Exception e) {
                handleGeneralError(null, e, errorMessageDTOList);
                totalDataFailed++;
            }
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public CustomerResponse createSingleApprove(CustomerApproveRequest approveRequest, String clientIP) {
        log.info("Approve for create billing customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        try {
            /* mapping from data JSON data after to class dto */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            CustomerDTO customerDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), CustomerDTO.class);

            /* check validation code and sub code already exists */
            List<String> validationErrors = new ArrayList<>();
            validationCustomerCodeAndSubCodeAlreadyExists(customerDTO.getCustomerCode(), customerDTO.getSubCode(), validationErrors);

            /* set data approval data change */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);

            /* check validation errors to custom response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                Customer customer = customerMapper.createEntity(customerDTO, dataChangeDTO);
                customerRepository.save(customer);
                dataChangeDTO.setDescription("Successfully approve data change and save data investment management with id: " + customer.getId());
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
                dataChangeDTO.setEntityId(customer.getId().toString());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageDTOList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public CustomerResponse updateSingleData(UpdateCustomerRequest updateCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update billing customer by id with request: {}", updateCustomerRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        try {
            /* map data from request to dto */
            CustomerDTO customerDTO = customerMapper.mapFromUpdateRequestToDto(updateCustomerRequest);
            CustomerDTO clonedDTO = new CustomerDTO();
            BeanUtil.copyAllProperties(customerDTO, clonedDTO);
            log.info("[Update Single] Result mapping request to dto: {}", customerDTO);

            /* get customer entity by id */
            Customer customer = customerRepository.findById(customerDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + customerDTO.getId()));

            /* data yang akan di validator */
            copyNonNullOrEmptyFields(customer, clonedDTO);
            log.info("[Update Single] Result map object entity to dto: {}", clonedDTO); // harapannya clonedDTO yang nilainya string kosong atau null, akan diisi oleh data entity

            /* check validator for data request after mapping to dto */
            List<String> validationErrors = new ArrayList<>();
            Errors errors = validateCustomerUsingValidator(clonedDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* validating selling agent is available or not */
            if (!StringUtils.isEmpty(clonedDTO.getSellingAgent())) {
                validationSellingAgentCodeAlreadyExists(clonedDTO.getSellingAgent(), validationErrors);
            }

            /* validating enums data */
            validateBillingEnums(clonedDTO.getBillingCategory(),
                    clonedDTO.getBillingType(),
                    clonedDTO.getCurrency(),
                    validationErrors);

            /* validating value GL must be true or false */
            if (isValidIsGLValue(clonedDTO.getGl())) {
                throw new InvalidInputException(INVALID_VALUE);
            }

            /* validating Cost Center Debit */
            validateGLForCostCenterDebit(Boolean.parseBoolean(clonedDTO.getGl()), clonedDTO.getDebitTransfer(), validationErrors);

            /* validating data billing template */
            validationBillingTemplate(clonedDTO.getBillingCategory(),
                    clonedDTO.getBillingType(),
                    clonedDTO.getCurrency(),
                    clonedDTO.getSubCode(),
                    clonedDTO.getBillingTemplate(),
                    validationErrors);

            /* validating data Investment Management is available or not */
            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(clonedDTO.getMiCode());
            log.info("Investment Management code: {}", investmentManagementDTO.getCode());

            /* set input id for data change */
            dataChangeDTO.setInputId(updateCustomerRequest.getInputId());

            /* check validation errors to custom response */
            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(clonedDTO.getCustomerCode(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonDataUpdate(objectMapper.writeValueAsString(customerDTO)));
                dataChangeService.createChangeActionEDIT(dataChangeDTO, Customer.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageDTOList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public CustomerResponse updateMultipleData(UpdateCustomerListRequest  updateCreateCustomerListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update multiple billing customer with request: {}", updateCreateCustomerListRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        for (UpdateCustomerDataListRequest updateCustomerDataListRequest : updateCreateCustomerListRequest.getUpdateCustomerDataListRequests()) {
            try {
                /* mapping data from request to dto */
                CustomerDTO customerDTO = customerMapper.mapFromUpdateRequestToDto(updateCustomerDataListRequest);
                log.info("[Update Multiple] Result mapping from request to dto: {}", customerDTO);

                /* get data by code and sub code */
                Customer customer = customerRepository.findByCustomerCodeAndOptionalSubCode(customerDTO.getCustomerCode(), customerDTO.getSubCode())
                        .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + customerDTO.getCustomerCode() + SUB_CODE_NOT_FOUND + customerDTO.getSubCode()));


                /* map data from dto entity, to overwrite new data */
                customerMapper.mapObjectsDtoToEntity(customerDTO, customer);
                log.info("[Update Multiple] Result map object dto to entity: {}", customer);
                CustomerDTO dto = customerMapper.mapToDto(customer);
                log.info("[Update Multiple] Result map object entity to dto: {}", dto);

                /* validating for each column dto */
                List<String> validationErrors = new ArrayList<>();
                Errors errors = validateCustomerUsingValidator(dto);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
                }

                /* validating selling agent is available or not */
                if (!StringUtils.isEmpty(dto.getSellingAgent())) {
                    validationSellingAgentCodeAlreadyExists(dto.getSellingAgent(), validationErrors);
                }

                /* validating enums data */
                validateBillingEnums(dto.getBillingCategory(),
                        dto.getBillingType(),
                        dto.getCurrency(),
                        validationErrors);

                /* validation value GL must be true or false */
                if (isValidIsGLValue(dto.getGl())) {
                    throw new InvalidInputException(INVALID_VALUE);
                }

                /* validating Cost Center Debit */
                validateGLForCostCenterDebit(Boolean.parseBoolean(dto.getGl()), dto.getDebitTransfer(), validationErrors);

                /* validating data billing template */
                validationBillingTemplate(dto.getBillingCategory(),
                        dto.getBillingType(),
                        dto.getCurrency(),
                        dto.getSubCode(),
                        dto.getBillingTemplate(),
                        validationErrors);

                /* validating data Investment Management */
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(dto.getMiCode());
                log.info("Investment Management code: {}", investmentManagementDTO.getCode());

                /* set input id for data change */
                dataChangeDTO.setInputId(dataChangeDTO.getInputId());

                if (!validationErrors.isEmpty()) {
                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), validationErrors);
                    errorMessageDTOList.add(errorMessageDTO);
                    totalDataFailed++;
                } else {
                    dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
                    dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonDataUpdate(objectMapper.writeValueAsString(customerDTO)));
                    dataChangeService.createChangeActionEDIT(dataChangeDTO, Customer.class);
                    totalDataSuccess++;
                }
            } catch (Exception e) {
                handleGeneralError(null, e, errorMessageDTOList);
                totalDataFailed++;
            }
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public CustomerResponse updateSingleApprove(CustomerApproveRequest approveRequest, String clientIP) {
        log.info("Approve multiple update billing customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        try {
            /* get data change by id and get json data after data */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            CustomerDTO customerDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), CustomerDTO.class);
            log.info("[Update Approve] Map data from JSON data after data change: {}", customerDTO);

            /* get data investment management name */
            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getMiCode());

            /* get customer by code and sub code */
            Customer customer = customerRepository.findByCustomerCodeAndOptionalSubCode(customerDTO.getCustomerCode(), customerDTO.getSubCode())
                    .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + customerDTO.getCustomerCode() + SUB_CODE_NOT_FOUND + customerDTO.getSubCode()));
            customer.setMiName(investmentManagementDTO.getName());

            customerMapper.mapObjectsDtoToEntity(customerDTO, customer);
            log.info("[Update Approve] Map object dto to entity: {}", customer);

            CustomerDTO dto = customerMapper.mapToDto(customer);
            log.info("[Update Approve] map from entity to dto: {}", dto);

            /* check validation each column */
            List<String> validationErrors = new ArrayList<>();
            Errors errors = validateCustomerUsingValidator(dto);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set data change information */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setEntityId(customer.getId().toString());

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                Customer customerUpdated = customerMapper.updateEntity(customer, dataChangeDTO);
                Customer customerSaved = customerRepository.save(customerUpdated);
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerSaved)));
                dataChangeDTO.setDescription("Successfully approved and update data entity with id: " + customerSaved.getId());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public CustomerResponse deleteSingleData(DeleteCustomerRequest deleteCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Delete single data billing customer with request: {}", deleteCustomerRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        try {
            /* get data by id */
            Long id = deleteCustomerRequest.getId();
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + id));

            dataChangeDTO.setInputId(deleteCustomerRequest.getInputId());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
            dataChangeDTO.setJsonDataAfter("");
            dataChangeDTO.setEntityId(customer.getId().toString());
            dataChangeService.createChangeActionDELETE(dataChangeDTO, Customer.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public CustomerResponse deleteSingleApprove(CustomerApproveRequest approveRequest, String clientIP) {
        log.info("Approve delete multiple billing customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        try {
            /* get data change by id and get Entity ID */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());

            Customer customer = customerRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
            dataChangeDTO.setDescription("Successfully approve data change and delete data entity with id: " + customer.getId());
            dataChangeService.approvalStatusIsApproved(dataChangeDTO);
            customerRepository.delete(customer);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    public Errors validateCustomerUsingValidator(CustomerDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "customerDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void validationCustomerCodeAndSubCodeAlreadyExists(String customerCode, String subCode, List<String> validationErrors) {
        if (isCodeAlreadyExists(customerCode, subCode)) {
            validationErrors.add("Billing Customer already taken with code: " + customerCode + ", and sub code: " + subCode);
        }
    }

    private void validationBillingTemplate(String category, String type, String currency, String subCode, String templateName, List<String> validationErrors) {
        if (!billingTemplateService.isExistsByCategoryAndTypeAndCurrencyAndSubCodeAndTemplateName(category, type, currency, subCode, templateName)) {
            validationErrors.add("Billing Template not found with category: " + category
                    + ", type: " + type
                    + ", currency: " + currency
                    + ", sub code: " + subCode
                    + ", and template name: " + templateName);
        }
    }

    private void validationSellingAgentCodeAlreadyExists(String sellingAgentCode, List<String> validationErrors) {
        if (!sellingAgentService.isCodeAlreadyExists(sellingAgentCode)) {
            validationErrors.add("Selling Agent not found with code: " + sellingAgentCode);
        }
    }

    private void validateDataChangeId(String dataChangeId) {
        if (!dataChangeService.existById(Long.valueOf(dataChangeId))) {
            log.info("Data Change ids not found");
            throw new DataNotFoundException("Data Change ids not found");
        }
    }

    private void handleGeneralError(CustomerDTO customerDTO, Exception e, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : UNKNOWN, validationErrors));
    }

    private void handleDataNotFoundException(CustomerDTO customerDTO, DataNotFoundException e, List<ErrorMessageDTO> errorMessageList) {
        log.error("Billing Customer not found with id: {}", customerDTO != null ? customerDTO.getCustomerCode(): UNKNOWN, e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : UNKNOWN, validationErrors));
    }

    private void validateBillingEnums(String billingCategory, String billingType, String currency, List<String> validationErrors) {
        if (EnumValidator.validateEnumBillingCategory(billingCategory)) {
            validationErrors.add("Billing Category enum not found with value: " + billingCategory);
        }
        if (EnumValidator.validateEnumBillingType(billingType)) {
            validationErrors.add("Billing Type enum not found with value: " + billingType);
        }
        if (EnumValidator.validateEnumCurrency(currency)) {
            validationErrors.add("Currency enum not found with value: " + currency);
        }
    }

    private void validateGLForCostCenterDebit(boolean isGL, String debitTransfer, List<String> validationErrors) {
        // Improved readability with comments
        if (isGL) {
            // Check if costCenterDebit is null or blank (empty string)
            if (debitTransfer == null || debitTransfer.isEmpty()) {
                validationErrors.add("Cost Center Debit is required when GL is true");
            }
        } else {
            // Check if costCenterDebit is not null or blank (for readability)
            if (!StringUtils.isEmpty(debitTransfer)) {
                validationErrors.add("Cost Center Debit must be blank when GL is false");
            }
        }
    }

    private boolean isValidIsGLValue(String isGL) {
        return !"TRUE".equalsIgnoreCase(isGL) && !"FALSE".equalsIgnoreCase(isGL);
    }

    // Method to copy non-null and non-empty fields
    public void copyNonNullOrEmptyFields(Customer customer, CustomerDTO customerDTO) {
        try {
            Map<String, String> entityProperties = org.apache.commons.beanutils.BeanUtils.describe(customer);

            for (Map.Entry<String, String> entry : entityProperties.entrySet()) {
                String propertyName = entry.getKey();
                String entityValue = entry.getValue();

                // Get the current value in the DTO
                String dtoValue = org.apache.commons.beanutils.BeanUtils.getProperty(customerDTO, propertyName);

                // Copy value from entity to DTO if DTO's value is null or empty
                if (isNullOrEmpty(dtoValue) && entityValue != null) {
                    org.apache.commons.beanutils.BeanUtils.setProperty(customerDTO, propertyName, entityValue);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new GeneralException("Failed while processing copy non null or empty fields", e);
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
