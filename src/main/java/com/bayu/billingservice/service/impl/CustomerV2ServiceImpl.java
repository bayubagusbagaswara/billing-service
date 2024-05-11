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
    private final SellingAgentService sellingAgentService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final CustomerMapper customerMapper;

    private static final String ID_NOT_FOUND = "Billing Customer not found with id: ";
    private static final String CODE_NOT_FOUND = "Billing Customer not found with code: ";
    private static final String UNKNOWN = "unknown";

    @Override
    public CustomerDTO testCreate(CustomerDTO dto) {
        Customer customer = customerMapper.mapToEntity(dto);
        log.info("Customer: {}", customer);
        customerRepository.save(customer);
        return customerMapper.mapToDTO(customer);
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
    public CreateCustomerListResponse createSingleData(CreateCustomerRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data billing customer with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        CustomerDTO customerDTO = customerMapper.mapFromCreateCustomerRequestToDTO(request);
        try {
            List<String> validationErrors = new ArrayList<>();
            validationCustomerCodeAlreadyExists(customerDTO.getCustomerCode(), validationErrors);
            validationInvestmentManagementCodeAlreadyExists(customerDTO.getInvestmentManagementCode(), validationErrors);

            if (validationErrors.isEmpty()) {
                dataChangeDTO.setInputId(request.getInputId());
                dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
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
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public CreateCustomerListResponse createMultipleData(CreateCustomerListRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create billing customer multiple data with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        for (CustomerDTO customerDTO : request.getCustomerDTOList()) {
            try {
                List<String> validationErrors = new ArrayList<>();
                validationCustomerCodeAlreadyExists(customerDTO.getCustomerCode(), validationErrors);
                validationInvestmentManagementCodeAlreadyExists(customerDTO.getInvestmentManagementCode(), validationErrors);

                if (validationErrors.isEmpty()) {
                    dataChangeDTO.setInputId(request.getInputId());
                    dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
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
        }
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public CreateCustomerListResponse createMultipleApprove(CreateCustomerListRequest requestList) {
        log.info("Approve multiple for create billing customer with request: {}", requestList);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        validateDataChangeIds(requestList.getCustomerDTOList());

        for (CustomerDTO customerDTO : requestList.getCustomerDTOList()) {
            try {
                List<String> validationErrors = new ArrayList<>();
                Errors errors = validateCustomerUsingValidator(customerDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
                }

                // validasi customer code gak boleh ada yg duplikat di database
                validationCustomerCodeAlreadyExists(customerDTO.getCustomerCode(), validationErrors);

                // validasi mi code
                validationInvestmentManagementCodeAlreadyExists(customerDTO.getInvestmentManagementCode(), validationErrors);
                InvestmentManagementDTO investmentManagementDTO = investmentManagementService.getByCode(customerDTO.getInvestmentManagementCode());
                customerDTO.setInvestmentManagementName(investmentManagementDTO.getName());

                // validasi selling agent
                validationSellingAgentCodeAlreadyExists(customerDTO.getSellingAgentCode(), validationErrors);

                // validasi enum
                validateBillingEnums(customerDTO, validationErrors);

                // get data change by id
                BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(investmentManagementDTO.getDataChangeId());
                dataChangeDTO.setApproveId(requestList.getApproveId());
                dataChangeDTO.setApproveIPAddress(requestList.getApproveIPAddress());

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
        }
        return new CreateCustomerListResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
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

    private void validationInvestmentManagementCodeAlreadyExists(String investmentManagementCode, List<String> validationErrors) {
        if (!investmentManagementService.isCodeAlreadyExists(investmentManagementCode)) {
            validationErrors.add("Investment Management not found with code: " + investmentManagementCode);
        }
    }

    private void validationSellingAgentCodeAlreadyExists(String sellingAgentCode, List<String> validationErrors) {
        if (!sellingAgentService.isCodeAlreadyExists(sellingAgentCode)) {
            validationErrors.add("Selling Agent not found with code: " + sellingAgentCode);
        }
    }

    private void validateDataChangeIds(List<CustomerDTO> customerDTOList) {
        List<Long> idDataChangeList = customerDTOList.stream()
                .map(CustomerDTO::getDataChangeId)
                .toList();

        if (!dataChangeService.areAllIdsExistInDatabase(idDataChangeList)) {
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
