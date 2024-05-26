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
import com.bayu.billingservice.util.BeanUtil;
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
        log.info("Approve when create selling agent with request: {}", sellingAgentApproveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        SellingAgentDTO sellingAgentDTO = null;

        try {
            /* validating data change id */
            validateDataChangeId(sellingAgentApproveRequest.getDataChangeId());

            /* mapping from json data after to dto */
            Long dataChangeId = Long.valueOf(sellingAgentApproveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            sellingAgentDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), SellingAgentDTO.class);

            /* check validation code already exists */
            validationCodeAlreadyExists(sellingAgentDTO.getCode(), validationErrors);

            /* set data change for approve id and approve ip address */
            dataChangeDTO.setApproveId(sellingAgentApproveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(sellingAgentDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                SellingAgent sellingAgent = sellingAgentMapper.createEntity(sellingAgentDTO, dataChangeDTO);
                sellingAgentRepository.save(sellingAgent);
                dataChangeDTO.setDescription("Successfully approve data change and save data selling agent with id: " + sellingAgent.getId());
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(sellingAgent)));
                dataChangeDTO.setEntityId(sellingAgent.getId().toString());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(sellingAgentDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new SellingAgentResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public SellingAgentResponse updateSingleData(UpdateSellingAgentRequest updateSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update single data selling agent with request: {}", updateSellingAgentRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        SellingAgentDTO clonedDTO = null;

        try {
            /* mapping data from request to dto */
            SellingAgentDTO sellingAgentDTO = sellingAgentMapper.mapUpdateRequestToDto(updateSellingAgentRequest);
            clonedDTO = new SellingAgentDTO();
            BeanUtil.copyAllProperties(sellingAgentDTO, clonedDTO);
            log.info("[Update Single] Result mapping request to dto: {}", sellingAgentDTO);

            /* get selling agent by id */
            SellingAgent sellingAgent = sellingAgentRepository.findById(sellingAgentDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + sellingAgentDTO.getId()));

            /* data yang akan di validator */
            copyNonNullOrEmptyFields(sellingAgent, clonedDTO);
            log.info("[Update Single] Result map object entity to dto: {}", clonedDTO);

            /* check validator for data request after mapping to dto */
            Errors errors = validateSellingAgentUsingValidator(clonedDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set input id for data change */
            dataChangeDTO.setInputId(updateSellingAgentRequest.getInputId());

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(sellingAgentDTO.getCode(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(sellingAgent)));
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonDataUpdate(objectMapper.writeValueAsString(sellingAgentDTO)));
                dataChangeDTO.setEntityId(sellingAgent.getId().toString());
                dataChangeService.createChangeActionEDIT(dataChangeDTO, SellingAgent.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(clonedDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new SellingAgentResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public SellingAgentResponse updateSingleApprove(SellingAgentApproveRequest sellingAgentApproveRequest, String clientIP) {
        log.info("Approve when update selling agent with request: {}", sellingAgentApproveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        SellingAgentDTO sellingAgentDTO = null;

        try {
            /* validating data change id */
            validateDataChangeId(sellingAgentApproveRequest.getDataChangeId());

            /* get data change by id and get json data after */
            Long dataChangeId = Long.valueOf(sellingAgentApproveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());
            SellingAgentDTO dto = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), SellingAgentDTO.class);
            log.info("[Update Approve] Map data from JSON data after: {}", dto);

            /* get selling agent entity by id */
            SellingAgent sellingAgent = sellingAgentRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            sellingAgentMapper.mapObjectsDtoToEntity(dto, sellingAgent);
            log.info("[Update Approve] Map object dto to entity: {}", sellingAgent);

            sellingAgentDTO = sellingAgentMapper.mapToDto(sellingAgent);
            log.info("[Update Approve] Map from entity to dto: {}", sellingAgentDTO);

            /* check validation each column dto */
            Errors errors = validateSellingAgentUsingValidator(sellingAgentDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set data change approve id and approve ip address */
            dataChangeDTO.setApproveId(sellingAgentApproveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setEntityId(sellingAgent.getId().toString());

            /* check validation error for customer response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(sellingAgentDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                SellingAgent sellingAgentUpdated = sellingAgentMapper.updateEntity(sellingAgent, dataChangeDTO);
                SellingAgent sellingAgentSaved = sellingAgentRepository.save(sellingAgentUpdated);
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(sellingAgentSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update selling agent entity with id: " + sellingAgentSaved.getId());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(sellingAgentDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new SellingAgentResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public SellingAgentResponse deleteSingleData(DeleteSellingAgentRequest deleteSellingAgentRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Delete single selling agent with request: {}", deleteSellingAgentRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        SellingAgentDTO sellingAgentDTO = null;

        try {
            /* get selling agent by id */
            Long id = deleteSellingAgentRequest.getId();
            SellingAgent sellingAgent = sellingAgentRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + id));

            /* mapping entity to dto */
            sellingAgentDTO = sellingAgentMapper.mapToDto(sellingAgent);

            /* set data change */
            dataChangeDTO.setInputId(deleteSellingAgentRequest.getInputId());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(sellingAgent)));
            dataChangeDTO.setJsonDataAfter("");
            dataChangeDTO.setEntityId(sellingAgent.getId().toString());
            dataChangeService.createChangeActionDELETE(dataChangeDTO, SellingAgent.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(sellingAgentDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new SellingAgentResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public SellingAgentResponse deleteSingleApprove(SellingAgentApproveRequest approveRequest, String clientIP) {
        log.info("Approve when delete selling agent with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        SellingAgentDTO sellingAgentDTO = null;

        try {
            /* validating data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* get data change by id and get Entity Id */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());

            /* get entity by id */
            SellingAgent sellingAgent = sellingAgentRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            /* mapping from entity to dto */
            sellingAgentDTO = sellingAgentMapper.mapToDto(sellingAgent);

            /* set data change for approve id and approve ip address */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(sellingAgent)));
            dataChangeDTO.setDescription("Successfully approve data change and delete selling agent with id: " + sellingAgent.getId());
            dataChangeService.approvalStatusIsApproved(dataChangeDTO);

            /* delete data entity in the database */
            sellingAgentRepository.delete(sellingAgent);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(sellingAgentDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new SellingAgentResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public String deleteAll() {
        sellingAgentRepository.deleteAll();
        return "Successfully delete all selling agent";
    }

    @Override
    public List<SellingAgentDTO> getAll() {
        List<SellingAgent> all = sellingAgentRepository.findAll();
        return sellingAgentMapper.mapToDTOList(all);
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
