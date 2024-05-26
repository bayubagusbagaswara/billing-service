package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.sellingagent.*;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.mapper.SellingAgentMapper;
import com.bayu.billingservice.model.SellingAgent;
import com.bayu.billingservice.repository.SellingAgentRepository;
import com.bayu.billingservice.service.DataChangeService;
import com.bayu.billingservice.service.SellingAgentService;
import com.bayu.billingservice.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellingAgentServiceImpl implements SellingAgentService {

    private static final String ID_NOT_FOUND = "Selling Agent not found with id: ";
    private static final String UNKNOWN = "unknown";

    private final SellingAgentRepository sellingAgentRepository;
    private final DataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final SellingAgentMapper sellingAgentMapper;

    @Override
    public boolean isCodeAlreadyExists(String sellingAgentCode) {
        return sellingAgentRepository.existsByCode(sellingAgentCode);
    }

    @Override
    public SellingAgentDTO getBySellingAgentCode(String sellingAgentCode) {
        SellingAgent sellingAgent = sellingAgentRepository.findByCode(sellingAgentCode)
                .orElseThrow(() -> new DataNotFoundException("Selling Agent not found with code : " + sellingAgentCode));
        return sellingAgentMapper.mapToDto(sellingAgent);
    }

    @Override
    public SellingAgentResponse createSingleData(CreateSellingAgentRequest createSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data selling agent with request: {}", createSellingAgentRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        SellingAgentDTO sellingAgentDTO = null;

        try {
            /* mapping data from request to dto */
            sellingAgentDTO = sellingAgentMapper.mapCreateRequestToDto(createSellingAgentRequest);

            /* validating for each column dto */
            Errors errors = validateSellingAgentUsingValidator(sellingAgentDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* validating code already exists */
            validationCodeAlreadyExists(sellingAgentDTO.getCode(), validationErrors);

            /* set data input id for data change */
            dataChangeDTO.setInputId(createSellingAgentRequest.getInputId());

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(sellingAgentDTO.getCode(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(sellingAgentDTO)));
                dataChangeService.createChangeActionADD(dataChangeDTO, SellingAgent.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(sellingAgentDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new SellingAgentResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public SellingAgentResponse createSingleApprove(SellingAgentApproveRequest sellingAgentApproveRequest, String clientIP) {
        return null;
    }

    @Override
    public SellingAgentResponse updateSingleData(UpdateSellingAgentRequest updateSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public SellingAgentResponse updateSingleApprove(SellingAgentApproveRequest sellingAgentApproveRequest, String clientIP) {
        return null;
    }

    @Override
    public SellingAgentResponse deleteSingleData(DeleteSellingAgentRequest deleteSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public SellingAgentResponse deleteSingleApprove(SellingAgentApproveRequest approveRequest, String clientIP) {
        return null;
    }

    @Override
    public String deleteAll() {
        return "";
    }

    @Override
    public List<SellingAgentDTO> getAll() {
        return List.of();
    }

    public Errors validateSellingAgentUsingValidator(SellingAgentDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "sellingAgentDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void validationCodeAlreadyExists(String code, List<String> errorMessages) {
        if (isCodeAlreadyExists(code)) {
            errorMessages.add("Selling Agent is already taken with code: " + code);
        }
    }

    private void validateDataChangeId(String dataChangeId) {
        if (dataChangeService.existById(Long.valueOf(dataChangeId))) {
            log.info("Data Change id not found");
            throw new DataNotFoundException("Data Change not found with id: " + dataChangeId);
        }
    }

    private void handleGeneralError(SellingAgentDTO sellingAgentDTO, Exception e, List<String> validationErrors, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(sellingAgentDTO != null ? sellingAgentDTO.getCode() : UNKNOWN, validationErrors));
    }

    // Method to copy non-null and non-empty fields
    public void copyNonNullOrEmptyFields(SellingAgent sellingAgent, SellingAgentDTO sellingAgentDTO) {
        try {
            Map<String, String> entityProperties = BeanUtils.describe(sellingAgent);

            for (Map.Entry<String, String> entry : entityProperties.entrySet()) {
                String propertyName = entry.getKey();
                String entityValue = entry.getValue();

                // Get the current value in the DTO
                String dtoValue = BeanUtils.getProperty(sellingAgentDTO, propertyName);

                // Copy value from entity to DTO if DTO's value is null or empty
                if (isNullOrEmpty(dtoValue) && entityValue != null) {
                    BeanUtils.setProperty(sellingAgentDTO, propertyName, entityValue);
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
