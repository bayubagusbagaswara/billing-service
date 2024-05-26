package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
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
import org.apache.commons.beanutils.BeanUtils;
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
    private static final String INVALID_VALUE_TRUE_OR_FALSE = "Invalid value for 'Is GL'. Value must be 'TRUE' or 'FALSE'.";
    private static final String INVESTMENT_MANAGEMENT_NOT_FOUND_WITH_CODE = "Investment Management not found with code: ";

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
        List<String> validationErrors = new ArrayList<>();
        CustomerDTO customerDTO = null;

        try {
            /* mapping data from request to dto */
            customerDTO = customerMapper.mapCreateRequestToDto(createCustomerRequest);
            log.info("[Create Single] Map from request to dto: {}", customerDTO);

            /* validation for each column dto */
            Errors errors = validateCustomerUsingValidator(customerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* validation code and sub code already exists */
            validationCustomerCodeAndSubCodeAlreadyExists(customerDTO.getCustomerCode(), customerDTO.getSubCode(), validationErrors);

            /* validating sales agent is available or not */
            validateSellingAgent(customerDTO, validationErrors);

            /* validation enum data */
            validateBillingEnums(customerDTO.getBillingCategory(),
                    customerDTO.getBillingType(),
                    customerDTO.getCurrency(),
                    validationErrors);

            /* validation value GL must be true or false */
            validateIsGL(customerDTO, validationErrors);

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
            handleGeneralError(customerDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public CustomerResponse createMultipleData(CreateCustomerListRequest createCustomerListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create billing customer multiple data with request: {}", createCustomerListRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        CustomerDTO customerDTO = null;

        /* repeat data one by one */
        for (CreateCustomerDataListRequest createCustomerDataListRequest : createCustomerListRequest.getCreateCustomerDataListRequests()) {
            try {
                /* mapping data from request to dto */
                customerDTO = customerMapper.mapCreateListRequestToDTO(createCustomerDataListRequest);
                log.info("[Create Multiple] mapper from create request to dto: {}", customerDTO);

                /* validating for each column dto */
                Errors errors = validateCustomerUsingValidator(customerDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
                }

                /* validating code and sub code already exists */
                validationCustomerCodeAndSubCodeAlreadyExists(customerDTO.getCustomerCode(), customerDTO.getSubCode(), validationErrors);

                /* validation selling agent is available or not */
                validateSellingAgent(customerDTO, validationErrors);

                /* validating enum data */
                validateBillingEnums(customerDTO.getBillingCategory(),
                        customerDTO.getBillingType(),
                        customerDTO.getCurrency(),
                        validationErrors);

                /* validating value GL must be true or false */
                validateIsGL(customerDTO, validationErrors);

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
                customerDTO.setMiName(investmentManagementDTO.getName());

                /* set data input id to data change */
                dataChangeDTO.setInputId(createCustomerListRequest.getInputId());

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
                handleGeneralError(customerDTO, e, validationErrors, errorMessageDTOList);
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
        List<String> validationErrors = new ArrayList<>();
        CustomerDTO customerDTO = null;

        try {
            /* validate data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* mapping from data JSON data after to class dto */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            customerDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), CustomerDTO.class);

            /* check validation code and sub code already exists */
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
            handleGeneralError(customerDTO, e, validationErrors, errorMessageDTOList);
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
        List<String> validationErrors = new ArrayList<>();
        CustomerDTO clonedDTO = null;

        try {
            /* map data from request to dto */
            CustomerDTO customerDTO = customerMapper.mapUpdateRequestToDto(updateCustomerRequest);
            clonedDTO = new CustomerDTO();
            BeanUtil.copyAllProperties(customerDTO, clonedDTO);
            log.info("[Update Single] Result mapping request to dto: {}", customerDTO);

            /* get customer entity by id */
            Customer customer = customerRepository.findById(customerDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + customerDTO.getId()));

            /* data yang akan di validator */
            copyNonNullOrEmptyFields(customer, clonedDTO);
            log.info("[Update Single] Result map object entity to dto: {}", clonedDTO);

            /* check validator for data request after mapping to dto */
            Errors errors = validateCustomerUsingValidator(clonedDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* validating selling agent is available or not */
            validateSellingAgent(clonedDTO, validationErrors);

            /* validating enums data */
            validateBillingEnums(clonedDTO.getBillingCategory(),
                    clonedDTO.getBillingType(),
                    clonedDTO.getCurrency(),
                    validationErrors);

            /* validating value GL must be true or false */
            validateIsGL(clonedDTO, validationErrors);

            /* validating Cost Center Debit */
            if (Boolean.FALSE.toString().equalsIgnoreCase(clonedDTO.getGl())) {
                clonedDTO.setDebitTransfer("");
            }
            validateGLForCostCenterDebit(Boolean.parseBoolean(clonedDTO.getGl()), clonedDTO.getDebitTransfer(), validationErrors);

            /* validating data billing template */
            validationBillingTemplate(clonedDTO.getBillingCategory(),
                    clonedDTO.getBillingType(),
                    clonedDTO.getCurrency(),
                    clonedDTO.getSubCode(),
                    clonedDTO.getBillingTemplate(),
                    validationErrors);

            /* validating data Investment Management is available or not */
            validateIsExistsInvestmentManagement(clonedDTO, validationErrors);

            /* set input id for data change */
            dataChangeDTO.setInputId(updateCustomerRequest.getInputId());
            dataChangeDTO.setEntityId(customer.getId().toString());

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
            handleGeneralError(clonedDTO, e, validationErrors, errorMessageDTOList);
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
        List<String> validationErrors = new ArrayList<>();
        CustomerDTO customerDTO = null;

        for (UpdateCustomerDataListRequest updateCustomerDataListRequest : updateCreateCustomerListRequest.getUpdateCustomerDataListRequests()) {
            try {
                /* mapping data from request to dto */
                customerDTO = customerMapper.mapUpdateListRequestToDTO(updateCustomerDataListRequest);
                log.info("[Update Multiple] Result mapping from request to dto: {}", customerDTO);

                /* get data by code and sub code */
                CustomerDTO finalCustomerDTO = customerDTO;
                Customer customer = customerRepository.findByCustomerCodeAndOptionalSubCode(customerDTO.getCustomerCode(), customerDTO.getSubCode())
                        .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + finalCustomerDTO.getCustomerCode() + SUB_CODE_NOT_FOUND + finalCustomerDTO.getSubCode()));

                /* cloned data customer entity */
                Customer clonedCustomer = new Customer();
                BeanUtil.copyAllProperties(customer, clonedCustomer);

                /* map data from dto entity, to overwrite new data */
                customerMapper.mapObjectsDtoToEntity(customerDTO, clonedCustomer);
                log.info("[Update Multiple] Result map object dto to entity: {}", clonedCustomer);
                CustomerDTO dto = customerMapper.mapToDto(clonedCustomer);
                log.info("[Update Multiple] Result map object entity to dto: {}", dto);

                /* validating for each column dto */
                Errors errors = validateCustomerUsingValidator(dto);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
                }

                /* validating selling agent is available or not */
                validateSellingAgent(dto, validationErrors);

                /* validating enums data */
                validateBillingEnums(dto.getBillingCategory(),
                        dto.getBillingType(),
                        dto.getCurrency(),
                        validationErrors);

                /* validation value GL must be true or false */
                validateIsGL(customerDTO, validationErrors);

                /* validating Cost Center Debit */
                if (Boolean.FALSE.toString().equalsIgnoreCase(dto.getGl())) {
                    dto.setDebitTransfer("");
                }
                validateGLForCostCenterDebit(Boolean.parseBoolean(dto.getGl()), dto.getDebitTransfer(), validationErrors);

                /* validating data billing template */
                validationBillingTemplate(dto.getBillingCategory(),
                        dto.getBillingType(),
                        dto.getCurrency(),
                        dto.getSubCode(),
                        dto.getBillingTemplate(),
                        validationErrors);

                /* validating data Investment Management is available or not */
                validateIsExistsInvestmentManagement(dto, validationErrors);

                /* set input id for data change */
                dataChangeDTO.setInputId(updateCreateCustomerListRequest.getInputId());
                dataChangeDTO.setEntityId(customer.getId().toString());

                /* check validation errors for custom response */
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
                handleGeneralError(customerDTO, e, validationErrors, errorMessageDTOList);
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
        List<String> validationErrors = new ArrayList<>();
        CustomerDTO customerDTO = null;

        try {
            /* validate data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* get data change by id and get json data after data */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            CustomerDTO dto = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), CustomerDTO.class);
            log.info("[Update Approve] Map data from JSON data after data change: {}", dto);

            /* get customer by id */
            Customer customer = customerRepository.findById(Long.valueOf(dataChangeDTO.getEntityId()))
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + dataChangeDTO.getEntityId()));

            /* set data investment management name */
            if (dto.getMiCode() != null && !dto.getMiCode().isEmpty()) {
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(dto.getMiCode());
                customer.setMiName(investmentManagementDTO.getName());
            }

            /* check and set debit transfer */
            if (dto.getGl() != null && !dto.getGl().isEmpty() && Boolean.FALSE.toString().equalsIgnoreCase(dto.getGl())) {
                customer.setDebitTransfer("");
            }

            customerMapper.mapObjectsDtoToEntity(dto, customer);
            log.info("[Update Approve] Map object dto to entity: {}", customer);

            customerDTO = customerMapper.mapToDto(customer);
            log.info("[Update Approve] map from entity to dto: {}", customerDTO);

            /* check validation each column */
            Errors errors = validateCustomerUsingValidator(customerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set data change information */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setEntityId(customer.getId().toString());

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(dto)));
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
            handleGeneralError(customerDTO, e, validationErrors, errorMessageList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public CustomerResponse deleteSingleData(DeleteCustomerRequest deleteCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Delete single data billing customer with request: {}", deleteCustomerRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        CustomerDTO customerDTO = null;

        try {
            /* get data by id */
            Long id = deleteCustomerRequest.getId();
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + id));

            customerDTO = customerMapper.mapToDto(customer);

            dataChangeDTO.setInputId(deleteCustomerRequest.getInputId());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
            dataChangeDTO.setJsonDataAfter("");
            dataChangeDTO.setEntityId(customer.getId().toString());
            dataChangeService.createChangeActionDELETE(dataChangeDTO, Customer.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(customerDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public CustomerResponse deleteSingleApprove(CustomerApproveRequest approveRequest, String clientIP) {
        log.info("Approve delete multiple billing customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        CustomerDTO customerDTO = null;

        try {
            /* validate data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* get data change by id and get Entity ID */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());

            Customer customer = customerRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));
            customerDTO = customerMapper.mapToDto(customer);

            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
            dataChangeDTO.setDescription("Successfully approve data change and delete data entity with id: " + customer.getId());
            dataChangeService.approvalStatusIsApproved(dataChangeDTO);
            customerRepository.delete(customer);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(customerDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
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

    private void validateSellingAgent(CustomerDTO customerDTO, List<String> validationErrors) {
        if (!StringUtils.isEmpty(customerDTO.getSellingAgent()) && !sellingAgentService.isCodeAlreadyExists(customerDTO.getSellingAgent())) {
                validationErrors.add("Selling Agent not found with code: " + customerDTO.getSellingAgent());
            }
    }

    private void validateDataChangeId(String dataChangeId) {
        if (dataChangeService.existById(Long.valueOf(dataChangeId))) {
            log.info("Data Change ids not found");
            throw new DataNotFoundException("Data Change ids not found");
        }
    }

    private void validateIsExistsInvestmentManagement(CustomerDTO customerDTO, List<String> validationErrors) {
        if (!investmentManagementService.isExistsByCode(customerDTO.getMiCode())) {
            validationErrors.add(INVESTMENT_MANAGEMENT_NOT_FOUND_WITH_CODE + customerDTO.getMiCode());
        }
    }

    private void handleGeneralError(CustomerDTO customerDTO, Exception e, List<String> validationErrors, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
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
        if (isGL) {
            if (debitTransfer == null || debitTransfer.isEmpty()) {
                validationErrors.add("Cost Center Debit is required when GL is true");
            }
        } else {
            if (!StringUtils.isEmpty(debitTransfer)) {
                validationErrors.add("Cost Center Debit must be blank when GL is false");
            }
        }
    }

    private void validateIsGL(CustomerDTO customerDTO, List<String> validationErrors) {
        if (isValidIsGLValue(customerDTO.getGl())) {
            validationErrors.add(INVALID_VALUE_TRUE_OR_FALSE);
        }
    }

    private boolean isValidIsGLValue(String isGL) {
        return !"TRUE".equalsIgnoreCase(isGL) && !"FALSE".equalsIgnoreCase(isGL);
    }

    public void copyNonNullOrEmptyFields(Customer customer, CustomerDTO customerDTO) {
        try {
            Map<String, String> entityProperties = BeanUtils.describe(customer);

            for (Map.Entry<String, String> entry : entityProperties.entrySet()) {
                String propertyName = entry.getKey();
                String entityValue = entry.getValue();

                String dtoValue = BeanUtils.getProperty(customerDTO, propertyName);

                if (isNullOrEmpty(dtoValue) && entityValue != null) {
                    BeanUtils.setProperty(customerDTO, propertyName, entityValue);
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
