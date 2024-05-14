package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.Customer;
import com.bayu.billingservice.repository.CustomerRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.service.CustomerV2Service;
import com.bayu.billingservice.service.InvestmentManagementService;
import com.bayu.billingservice.service.SellingAgentService;
import com.bayu.billingservice.mapper.CustomerMapper;
import com.bayu.billingservice.util.EnumValidator;
import com.bayu.billingservice.util.JsonUtil;
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
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerV2ServiceImpl implements CustomerV2Service {

    private static final String ID_NOT_FOUND = "Billing Customer not found with id: ";
    private static final String CODE_NOT_FOUND = "Billing Customer not found with code: ";
    private static final String UNKNOWN = "unknown";

    private final CustomerRepository customerRepository;
    private final BillingDataChangeService dataChangeService;
    private final InvestmentManagementService investmentManagementService;
    private final SellingAgentService sellingAgentService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerDTO testCreate(CustomerDTO dto) {
        Customer customer = customerMapper.mapToEntity(dto);
        log.info("Customer: {}", customer);
        customerRepository.save(customer);
        return customerMapper.mapToDto(customer);
    }

    @Override
    public List<CustomerDTO> getAllTest() {
        List<Customer> all = customerRepository.findAll();
        return customerMapper.mapToDTOList(all);
    }

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return customerRepository.existsByCustomerCode(code);
    }

    @Override
    public List<CustomerDTO> getAll() {
        return List.of();
    }

    @Override
    public String deleteAll() {
        customerRepository.deleteAll();
        return "Successfully delete all billing customer;";
    }

    @Override
    public CustomerResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data billing customer with request: {}", request);
        return processDataChangeForCustomer(request, dataChangeDTO);
    }

    @Override
    public CustomerResponse createMultipleData(CustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create billing customer multiple data with request: {}", request);
        return processDataChangeForCustomerList(request.getCustomerDTOList(), dataChangeDTO);
    }

    private CustomerResponse processDataChangeForCustomer(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        CustomerDTO customerDTO = customerMapper.mapFromCreateRequestToDto(request);
        return processDataChangeForCustomerDTO(customerDTO, dataChangeDTO);
    }

    private CustomerResponse processDataChangeForCustomerList(List<CustomerDTO> customerDTOList, BillingDataChangeDTO dataChangeDTO) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        for (CustomerDTO customerDTO : customerDTOList) {
            CustomerResponse response = processDataChangeForCustomerDTO(customerDTO, dataChangeDTO);
            totalDataSuccess += response.getTotalDataSuccess();
            totalDataFailed += response.getTotalDataFailed();
            errorMessageDTOList.addAll(response.getErrorMessageDTOList());
        }

        return new CustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    private CustomerResponse processDataChangeForCustomerDTO(CustomerDTO customerDTO, BillingDataChangeDTO dataChangeDTO) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        try {
            List<String> validationErrors = new ArrayList<>();
            validationCustomerCodeAlreadyExists(customerDTO.getCustomerCode(), validationErrors);

            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getInvestmentManagementCode());
            customerDTO.setInvestmentManagementCode(investmentManagementDTO.getCode());

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
    public CustomerResponse createSingleApprove(CustomerApproveRequest approveRequest) {
        log.info("Approve multiple for create billing customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());

        CustomerDTO customerDTO = approveRequest.getData();
        try {
            List<String> validationErrors = new ArrayList<>();

            // validasi not empty
            Errors errors = validateCustomerUsingValidator(customerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            // validasi customer code gak boleh ada yg duplikat di database
            validationCustomerCodeAlreadyExists(customerDTO.getCustomerCode(), validationErrors);

            // validasi mi code dan dapatkan nilai name
            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getInvestmentManagementCode());
            customerDTO.setInvestmentManagementCode(investmentManagementDTO.getCode());
            customerDTO.setInvestmentManagementName(investmentManagementDTO.getName());

            // validasi selling agent
            validationSellingAgentCodeAlreadyExists(customerDTO.getSellingAgentCode(), validationErrors);

            // validasi enum
            validateBillingEnums(customerDTO, validationErrors);

            // get data change by id
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(Long.valueOf(approveRequest.getDataChangeId()));
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
            handleGeneralError(customerDTO, e, errorMessageDTOList);
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
    public CustomerResponse updateMultipleData(CustomerListRequest updateCustomerListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update multiple billing customer with request: {}", updateCustomerListRequest);
        dataChangeDTO.setInputId(updateCustomerListRequest.getInputId());
        dataChangeDTO.setInputIPAddress(updateCustomerListRequest.getInputIPAddress());
        return processUpdateForCustomerList(updateCustomerListRequest.getCustomerDTOList(), dataChangeDTO);
    }

    private CustomerResponse processUpdateForCustomerList(List<CustomerDTO> customerDTOList, BillingDataChangeDTO dataChangeDTO) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (CustomerDTO customerDTO : customerDTOList) {
            try {
                Customer customer = customerRepository.findById(customerDTO.getId())
                        .orElseThrow(() -> new DataNotFoundException("Customer not found with ID: " + customerDTO.getId()));

                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getInvestmentManagementCode());
                customerDTO.setInvestmentManagementCode(investmentManagementDTO.getCode());

                updateCustomerAndDataChange(customer, customerDTO, dataChangeDTO);
                totalDataSuccess++;
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
    public CustomerResponse updateSingleApprove(CustomerApproveRequest approveRequest) {
        log.info("Approve multiple update billing customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());

        CustomerDTO customerDTO = approveRequest.getData();
        try {
            List<String> validationErrors = new ArrayList<>();
            Errors errors = validateCustomerUsingValidator(customerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            // get customer by code, object Customer inilah yang akan diupdate
            Customer customer = customerRepository.findByCustomerCode(customerDTO.getCustomerCode())
                    .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + customerDTO.getCustomerCode()));

            customerMapper.mapObjects(customerDTO, customer);
            log.info("Customer after copy properties: {}", customer);

            // Validation MI code dan get name value
            InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customer.getInvestmentManagementCode());
            customer.setInvestmentManagementCode(investmentManagementDTO.getCode());
            customer.setInvestmentManagementName(investmentManagementDTO.getCode());

            // Validation selling agent
            validationSellingAgentCodeAlreadyExists(customerDTO.getSellingAgentCode(), validationErrors);

            // Validation ENUM
            validateBillingEnums(customerDTO, validationErrors);

            // Retrieve and set billing data change
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(Long.valueOf(approveRequest.getDataChangeId()));
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
            handleGeneralError(customerDTO, e, errorMessageList);
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
    public CustomerResponse deleteSingleApprove(CustomerApproveRequest approveRequest) {
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

    private void validationCustomerCodeAlreadyExists(String customerCode, List<String> validationErrors) {
        if (isCodeAlreadyExists(customerCode)) {
            validationErrors.add("Billing Customer already taken with code: " + customerCode);
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
        validationErrors.add("An unexpected error occurred: " + e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : UNKNOWN, validationErrors));
    }

    private void handleDataNotFoundException(CustomerDTO customerDTO, DataNotFoundException e, List<ErrorMessageDTO> errorMessageList) {
        log.error("Billing Customer not found with id: {}", customerDTO != null ? customerDTO.getCustomerCode(): UNKNOWN, e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(customerDTO != null ? customerDTO.getCustomerCode() : UNKNOWN, validationErrors));
    }

    private void validateBillingEnums(CustomerDTO customerDTO, List<String> validationErrors) {
        if (EnumValidator.validateEnumBillingCategory(customerDTO.getBillingCategory())) {
            validationErrors.add("Billing Category enum not found with value: " + customerDTO.getBillingCategory());
        }
        if (EnumValidator.validateEnumBillingType(customerDTO.getBillingType())) {
            validationErrors.add("Billing Type enum not found with value: " + customerDTO.getBillingType());
        }
        if (EnumValidator.validateEnumBillingTemplate(customerDTO.getBillingTemplate())) {
            validationErrors.add("Billing Template enum not found with value '" + customerDTO.getBillingTemplate());
        }
        if (EnumValidator.validateEnumCurrency(customerDTO.getCurrency())) {
            validationErrors.add("Currency enum not found with value '" + customerDTO.getCurrency());
        }
    }

}
