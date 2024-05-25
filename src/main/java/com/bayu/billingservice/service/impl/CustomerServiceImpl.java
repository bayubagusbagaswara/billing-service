package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.InvalidInputException;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.repository.CustomerRepository;
import com.bayu.billingservice.service.*;
import com.bayu.billingservice.mapper.CustomerMapper;
import com.bayu.billingservice.util.EnumValidator;
import com.bayu.billingservice.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private static final String ID_NOT_FOUND = "Billing Customer not found with id: ";
    private static final String CODE_NOT_FOUND = "Billing Customer not found with code: ";
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
            validationCustomerCodeAlreadyExists(customerDTO.getCustomerCode(), customerDTO.getSubCode(), validationErrors);

            /* validating sales agent is available or not */
            if (!StringUtils.isEmpty(customerDTO.getSellingAgent())) {
                validationSellingAgentCodeAlreadyExists(customerDTO.getSellingAgent(), validationErrors);
            }

            /* validation enum data */
            validateBillingEnums(customerDTO.getBillingCategory(), customerDTO.getBillingType(), customerDTO.getBillingTemplate(), customerDTO.getCurrency(), validationErrors);

            /* validation value GL must be true or false */
            if (!isValidIsGLValue(customerDTO.getGl())) {
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
        dataChangeDTO.setInputId(request.getInputId());
        dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        for (CreateCustomerDataListRequest createCustomerDataListRequest : request.getCreateCustomerDataListRequests()) {
            log.info("Before mapper: {}", createCustomerDataListRequest);
            CustomerDTO customerDTO = customerMapper.mapFromDataListToDTO(createCustomerDataListRequest);
            log.info("Mapper customer dto: {}", customerDTO);
            CustomerResponse response = processCustomerCreation(customerDTO, dataChangeDTO);
            totalDataSuccess += response.getTotalDataSuccess();
            totalDataFailed += response.getTotalDataFailed();
            errorMessageDTOList.addAll(response.getErrorMessageDTOList());
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    private CustomerResponse processCustomerCreation(CustomerDTO customerDTO, BillingDataChangeDTO dataChangeDTO) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        try {
            List<String> validationErrors = new ArrayList<>();

            // validation column dto
            Errors errors = validateCustomerUsingValidator(customerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            // validation code and sub code already exists
            validationCustomerCodeAlreadyExists(customerDTO.getCustomerCode(), customerDTO.getSubCode(), validationErrors);

            // validation selling agent
            if (!StringUtils.isEmpty(customerDTO.getSellingAgent())) {
                validationSellingAgentCodeAlreadyExists(customerDTO.getSellingAgent(), validationErrors);
            }

            log.info("Customer DTO: {}", customerDTO);
            // validation enum
            validateBillingEnums(customerDTO.getBillingCategory(), customerDTO.getBillingType(), customerDTO.getBillingTemplate(), customerDTO.getCurrency(), validationErrors);

            // Validasi nilai isGL
            if (!isValidIsGLValue(customerDTO.getGl())) {
                throw new InvalidInputException("Invalid value for isGL. Value must be 'TRUE' or 'FALSE'.");
            }

            // validation GL Cost Center Debit
            validateGLForCostCenterDebit(isValidIsGLValue(customerDTO.getGl()), customerDTO.getDebitTransfer(), validationErrors);

            // validation billing template
            validationBillingTemplate(customerDTO.getBillingCategory(), customerDTO.getBillingType(), customerDTO.getSubCode(), validationErrors);

            // validation Investment Management
            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getMiCode());
            customerDTO.setMiCode(investmentManagementDTO.getCode());
            customerDTO.setMiName(investmentManagementDTO.getName());

            if (validationErrors.isEmpty()) {
                dataChangeDTO.setInputId(dataChangeDTO.getInputId());
                dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerDTO)));

                dataChangeService.createChangeActionADD(dataChangeDTO, Customer.class);
                totalDataSuccess++;
            } else {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(customerDTO.getCustomerCode(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            }
        } catch (Exception e) {
            handleGeneralError(customerDTO, e, errorMessageDTOList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }


    @Override
    public CustomerResponse createSingleApprove(CustomerApproveRequest approveRequest, String clientIP) {
        log.info("Approve multiple for create billing customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());

        try {
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            List<String> validationErrors = new ArrayList<>();

            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            CustomerDTO customerDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), CustomerDTO.class);

            // validation customer code and cub code
            validationCustomerCodeAlreadyExists(customerDTO.getCustomerCode(), customerDTO.getSubCode(), validationErrors);

            // validation selling agent
            if (!StringUtils.isEmpty(customerDTO.getSellingAgent())) {
                validationSellingAgentCodeAlreadyExists(customerDTO.getSellingAgent(), validationErrors);
            }

            // validation enum
            validateBillingEnums(customerDTO.getBillingCategory(), customerDTO.getBillingType(), customerDTO.getBillingTemplate(), customerDTO.getCurrency(), validationErrors);

            // validation GL Cost Center Debit
            validateGLForCostCenterDebit(isValidIsGLValue(customerDTO.getGl()), customerDTO.getDebitTransfer(), validationErrors);

            // validation billing template dengan cara get billing template service by category dan type
            validationBillingTemplate(customerDTO.getBillingCategory(), customerDTO.getBillingType(), customerDTO.getSubCode(), validationErrors);

            // validation MI code
            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getMiCode());
            customerDTO.setMiCode(investmentManagementDTO.getCode());
            customerDTO.setMiName(investmentManagementDTO.getName());

            // set data approval data change
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                Customer customer = customerMapper.createEntity(customerDTO, dataChangeDTO);
                customerRepository.save(customer);

                dataChangeDTO.setDescription("Successfully approve data change and save data investment management");
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
    public CustomerResponse updateSingleData(UpdateCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update billing customer by id with request: {}", request);
        dataChangeDTO.setInputId(request.getInputId());
        dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
        CustomerDTO customerDTO = customerMapper.mapFromUpdateRequestToDto(request);
        return processUpdateForCustomerList(Collections.singletonList(customerDTO), dataChangeDTO);
    }

    @Override
    public CustomerResponse updateMultipleData(UpdateCustomerListRequest  updateCreateCustomerListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update multiple billing customer with request: {}", updateCreateCustomerListRequest);
        dataChangeDTO.setInputId(updateCreateCustomerListRequest.getInputId());
        dataChangeDTO.setInputIPAddress(updateCreateCustomerListRequest.getInputIPAddress());
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        for (UpdateCustomerDataListRequest updateCustomerDataListRequest : updateCreateCustomerListRequest.getUpdateCustomerDataListRequests()) {
            log.info("Update Customer Data List Request: {}", updateCustomerDataListRequest);
            customerDTOList.add(customerMapper.mapFromUpdateRequestToDto(updateCustomerDataListRequest));
        }
        log.info("Customer DTO List: {}", customerDTOList);
        return processUpdateForCustomerList(customerDTOList, dataChangeDTO);
    }

    private CustomerResponse processUpdateForCustomerList(List<CustomerDTO> customerDTOList, BillingDataChangeDTO dataChangeDTO) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (CustomerDTO customerDTO : customerDTOList) {
            try {
                List<String> validationErrors = new ArrayList<>();

                log.info("Customer DTO want to update: {}", customerDTO);

                Customer originalCustomer = customerRepository.findByCustomerCodeAndOptionalSubCode(customerDTO.getCustomerCode(), customerDTO.getSubCode())
                        .orElseThrow(() -> new DataNotFoundException("Customer not found with customer code: " + customerDTO.getCustomerCode() + ", and sub code: " + customerDTO.getSubCode()));

                log.info("Original Customer from database: {}", originalCustomer);

                Customer clonedCustomer = new Customer();
                BeanUtils.copyProperties(originalCustomer, clonedCustomer);

                if (customerDTO.getGl() != null && !customerDTO.getGl().isEmpty()) {
                    if (!isValidIsGLValue(customerDTO.getGl())) {
                        throw new InvalidInputException("Invalid value for isGL. Value must be 'TRUE' or 'FALSE'.");
                    }
                } else {
                    log.info("GL is empty or null");
                }

                // map from dto to cloned
                customerMapper.mapObjectsDtoToEntity(customerDTO, clonedCustomer);

                log.info("Replace between Customer DTO to Customer Entity: {}", clonedCustomer);

                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(clonedCustomer.getMiCode());
                log.info("Investment Management for update customer: {}", investmentManagementDTO.getCode());

                if (!StringUtils.isEmpty(clonedCustomer.getSellingAgent())) {
                    validationSellingAgentCodeAlreadyExists(clonedCustomer.getSellingAgent(), validationErrors);
                }

                if (Boolean.FALSE.equals(clonedCustomer.isGl())) {
                    clonedCustomer.setDebitTransfer("");
                    customerDTO.setDebitTransfer("");
                }

                // validation GL
                validateGLForCostCenterDebit(clonedCustomer.isGl(), clonedCustomer.getDebitTransfer(), validationErrors);

                // validation billing template
                validationBillingTemplate(clonedCustomer.getBillingCategory(), clonedCustomer.getBillingType(), clonedCustomer.getSubCode(), validationErrors);

                // validation enum
                validateBillingEnums(clonedCustomer.getBillingCategory(), clonedCustomer.getBillingType(), clonedCustomer.getBillingTemplate(), clonedCustomer.getCurrency(), validationErrors);

                if (!validationErrors.isEmpty()) {
                    ErrorMessageDTO errorMessageDTO = ErrorMessageDTO.builder()
                            .code(customerDTO.getCustomerCode())
                            .errorMessages(validationErrors)
                            .build();
                    errorMessageList.add(errorMessageDTO);
                    totalDataFailed++;
                } else {
                    updateCustomerAndDataChange(originalCustomer, customerDTO, dataChangeDTO);
                    totalDataSuccess++;
                }
            } catch (Exception e) {
                handleGeneralError(customerDTO, e, errorMessageList);
                totalDataFailed++;
            }
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private void updateCustomerAndDataChange(Customer customer, CustomerDTO customerDTO, BillingDataChangeDTO dataChangeDTO) throws JsonProcessingException {
        dataChangeDTO.setInputId(dataChangeDTO.getInputId());
        dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
        dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerDTO)));

        dataChangeService.createChangeActionEDIT(dataChangeDTO, Customer.class);
    }

    @Override
    public CustomerResponse updateSingleApprove(CustomerApproveRequest approveRequest, String clientIP) {
        log.info("Approve multiple update billing customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());

        try {
            List<String> validationErrors = new ArrayList<>();
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());

            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);

            // Hasil JSON After
            CustomerDTO customerDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), CustomerDTO.class);
            log.info("Hasil baca JSON Data After: {}", customerDTO);

            Customer customer = customerRepository.findByCustomerCode(customerDTO.getCustomerCode())
                    .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + customerDTO.getCustomerCode()));

            customerMapper.mapObjectsDtoToEntity(customerDTO, customer);
            log.info("Customer after copy properties: {}", customer);

            // Validation MI code dan get name value
            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customer.getMiCode());
            customer.setMiCode(investmentManagementDTO.getCode());
            customer.setMiName(investmentManagementDTO.getName());

            // Jika nilai Is GL adalah FALSE, hapus data di kolom Cost Center Debit
            if (Boolean.FALSE.equals(customer.isGl())) {
                customer.setDebitTransfer("");
            }

            // validasi hanya bisa dilakukan terhadap object customer hasil mapping data
            validateBillingEnums(customer.getBillingCategory(), customer.getBillingType(), customer.getBillingTemplate(), customer.getCurrency(), validationErrors);

            // Retrieve and set billing data change
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setEntityId(customer.getId().toString());

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
            } else {
                Customer customerUpdated = customerMapper.updateEntity(customer, dataChangeDTO);
                Customer customerSaved = customerRepository.save(customerUpdated);

                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customerSaved)));
                dataChangeDTO.setDescription("Successfully approved and update data entity");
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

        CustomerDTO customerDTO = CustomerDTO.builder()
                .id(deleteCustomerRequest.getId())
                .build();
        try {
            Customer customer = customerRepository.findById(customerDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + customerDTO.getId()));

            dataChangeDTO.setInputId(deleteCustomerRequest.getInputId());
            dataChangeDTO.setInputIPAddress(deleteCustomerRequest.getInputIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
            dataChangeDTO.setJsonDataAfter("");
            dataChangeDTO.setEntityId(customer.getId().toString());

            dataChangeService.createChangeActionDELETE(dataChangeDTO, Customer.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(customerDTO, e, errorMessageList);
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
        CustomerDTO customerDTO = approveRequest.getData();
        BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(Long.valueOf(approveRequest.getDataChangeId()));

        try {
            Customer customer = customerRepository.findById(customerDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + customerDTO.getId()));

            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(customer)));
            dataChangeDTO.setDescription("Successfully approve data change and delete data entity");

            dataChangeService.approvalStatusIsApproved(dataChangeDTO);
            customerRepository.delete(customer);
            totalDataSuccess++;
        } catch (DataNotFoundException e) {
            handleDataNotFoundException(customerDTO, e, errorMessageList);
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setApproveDate(new Date());
            List<String> validationErrors = new ArrayList<>();
            validationErrors.add(ID_NOT_FOUND + customerDTO.getId());
            dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
            totalDataFailed++;
        } catch (Exception e) {
            handleGeneralError(customerDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    public Errors validateCustomerUsingValidator(CustomerDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "customerDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void validationCustomerCodeAlreadyExists(String customerCode, String subCode, List<String> validationErrors) {
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

    private void validateBillingEnums(String billingCategory, String billingType, String billingTemplate, String currency, List<String> validationErrors) {
        if (EnumValidator.validateEnumBillingCategory(billingCategory)) {
            validationErrors.add("Billing Category enum not found with value: " + billingCategory);
        }
        if (EnumValidator.validateEnumBillingType(billingType)) {
            validationErrors.add("Billing Type enum not found with value: " + billingType);
        }
        if (EnumValidator.validateEnumBillingTemplate(billingTemplate)) {
            validationErrors.add("Billing Template enum not found with value: " + billingTemplate);
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
        return "TRUE".equalsIgnoreCase(isGL) || "FALSE".equalsIgnoreCase(isGL);
    }
}
