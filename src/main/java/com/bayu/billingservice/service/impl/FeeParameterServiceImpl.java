package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeparameter.*;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.mapper.FeeParameterMapper;
import com.bayu.billingservice.model.FeeParameter;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.FeeParameterRepository;
import com.bayu.billingservice.service.DataChangeService;
import com.bayu.billingservice.service.FeeParameterService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeParameterServiceImpl implements FeeParameterService {

    private static final String ID_NOT_FOUND = "Fee Parameter not found with id: ";
    private static final String CODE_NOT_FOUND = "Fee Parameter not found with code: ";
    private static final String UNKNOWN = "unknown";

    private final FeeParameterRepository feeParameterRepository;
    private final DataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final FeeParameterMapper feeParameterMapper;

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return feeParameterRepository.existsByCode(code);
    }

    @Override
    public boolean isNameAlreadyExists(String name) {
        return feeParameterRepository.existsByName(name);
    }

    @Override
    public FeeParameterResponse createSingleData(CreateFeeParameterRequest createFeeParameterRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data fee parameter with request: {}", createFeeParameterRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeParameterDTO feeParameterDTO = null;

        try {
            /* mapping data from request to dto */
            feeParameterDTO = feeParameterMapper.mapCreateRequestToDto(createFeeParameterRequest);
            log.info("[Create Single] Map create request to dto: {}", feeParameterDTO);

            /* validating for each column dto */
            Errors errors = validateFeeParameterUsingValidator(feeParameterDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* validating code already exists */
            validationCodeAlreadyExists(feeParameterDTO.getFeeCode(), validationErrors);

            /* validating name already exists */
            validationNameAlreadyExists(feeParameterDTO.getFeeName(), validationErrors);

            /* set data input id for data change */
            dataChangeDTO.setInputId(createFeeParameterRequest.getInputId());

            /* check validation errors for custom response */
            if (validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterDTO)));
                dataChangeService.createChangeActionADD(dataChangeDTO, FeeParameter.class);
                totalDataSuccess++;
            } else {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(feeParameterDTO.getFeeCode(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            }
        } catch (Exception e) {
            handleGeneralError(feeParameterDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }

        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public FeeParameterResponse createMultipleData(CreateFeeParameterListRequest createFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create multiple fee parameter with request: {}", createFeeParameterListRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeParameterDTO feeParameterDTO = null;

        for (CreateFeeParameterDataListRequest createFeeParameterDataListRequest : createFeeParameterListRequest.getCreateFeeParameterDataListRequests()) {
            try {
                /* mapping data from request to dto */
                feeParameterDTO = feeParameterMapper.mapCreateListRequestToDTO(createFeeParameterDataListRequest);

                /* validating for each column dto */
                Errors errors = validateFeeParameterUsingValidator(feeParameterDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
                }

                /* validating code already exists */
                validationCodeAlreadyExists(feeParameterDTO.getFeeCode(), validationErrors);

                /* validating name already exists */
                validationNameAlreadyExists(feeParameterDTO.getFeeName(), validationErrors);

                /* set input id to data change */
                dataChangeDTO.setInputId(createFeeParameterListRequest.getInputId());

                /* check validation errors for custom response */
                if (!validationErrors.isEmpty()) {
                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(feeParameterDTO.getFeeCode(), validationErrors);
                    errorMessageDTOList.add(errorMessageDTO);
                    totalDataFailed++;
                } else {
                    dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterDTO)));
                    dataChangeService.createChangeActionADD(dataChangeDTO, FeeParameter.class);
                    totalDataSuccess++;
                }
            } catch (Exception e) {
                handleGeneralError(feeParameterDTO, e, validationErrors, errorMessageDTOList);
                totalDataFailed++;
            }
        }
        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public FeeParameterResponse createSingleApprove(FeeParameterApproveRequest approveRequest, String clientIP) {
        log.info("Approve single fee parameter with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeParameterDTO feeParameterDTO = null;

        try {
            /* validate data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* mapping from JSON Data After to class dto */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            feeParameterDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), FeeParameterDTO.class);

            /* check validation code already exists */
            validationCodeAlreadyExists(feeParameterDTO.getFeeCode(), validationErrors);

            /* check validation name already exists */
            validationNameAlreadyExists(feeParameterDTO.getFeeName(), validationErrors);

            /* set data change for approve id and approve ip address */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                FeeParameter feeParameter = feeParameterMapper.createEntity(feeParameterDTO, dataChangeDTO);
                feeParameterRepository.save(feeParameter);
                dataChangeDTO.setDescription("Successfully approve data change and save data fee parameter with id: " + feeParameter.getId());
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameter)));
                dataChangeDTO.setEntityId(feeParameter.getId().toString());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(feeParameterDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public FeeParameterResponse updateSingleData(UpdateFeeParameterRequest updateFeeParameterRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update single data fee parameter with request: {}", updateFeeParameterRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeParameterDTO clonedDTO = null;

        try {
            FeeParameterDTO feeParameterDTO = feeParameterMapper.mapUpdateRequestToDto(updateFeeParameterRequest);
            clonedDTO = new FeeParameterDTO();
            BeanUtil.copyAllProperties(feeParameterDTO, clonedDTO);
            log.info("[Update Single] Result mapping request to dto: {}", feeParameterDTO);

            /* get fee parameter by id */
            FeeParameter feeParameter = feeParameterRepository.findById(feeParameterDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + feeParameterDTO.getId()));

            /* data yang akan di validator */
            copyNonNullOrEmptyFields(feeParameter, clonedDTO);
            log.info("[Update Single] Result map object entity to dto: {}", clonedDTO);

            /* check validator for data request after mapping to dto */
            Errors errors = validateFeeParameterUsingValidator(clonedDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set input id for data change */
            dataChangeDTO.setInputId(updateFeeParameterRequest.getInputId());

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(feeParameterDTO.getFeeCode(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameter)));
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonDataUpdate(objectMapper.writeValueAsString(feeParameterDTO)));
                dataChangeDTO.setEntityId(feeParameter.getId().toString());
                dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(clonedDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public FeeParameterResponse updateMultipleData(UpdateFeeParameterListRequest feeParameterListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update multiple data fee parameter with request: {}", feeParameterListRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeParameterDTO feeParameterDTO = null;

        /* repeat data one by one */
        for (UpdateFeeParameterDataListRequest updateFeeParameterDataListRequest : feeParameterListRequest.getUpdateFeeParameterDataListRequests()) {
            try {
                /* mapping data from request to dto */
                feeParameterDTO = feeParameterMapper.mapUpdateListRequestToDTO(updateFeeParameterDataListRequest);
                log.info("[Update Multiple] Result mapping from request to dto: {}", feeParameterDTO);

                /* get data by code */
                FeeParameterDTO finalFeeParameterDTO = feeParameterDTO;
                FeeParameter feeParameter = feeParameterRepository.findByCode(feeParameterDTO.getFeeCode())
                        .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + finalFeeParameterDTO.getFeeCode()));
                log.info("Entity: {}", feeParameter);

                /* map data from dto to entity, to overwrite new data */
                feeParameterMapper.mapObjectsDtoToEntity(feeParameterDTO, feeParameter);
                log.info("[Update Multiple] Result map object dto to entity: {}", feeParameter);
                FeeParameterDTO dto = feeParameterMapper.mapToDto(feeParameter);
                log.info("[Update Multiple] Result map object entity to dto: {}", dto);

                /* check validation data dto */
                Errors errors = validateFeeParameterUsingValidator(dto);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
                }

                /* set input id to data change */
                dataChangeDTO.setInputId(feeParameterListRequest.getInputId());

                /* check validation errors for custom response */
                if (!validationErrors.isEmpty()) {
                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(feeParameterDTO.getFeeCode(), validationErrors);
                    errorMessageDTOList.add(errorMessageDTO);
                    totalDataFailed++;
                } else {
                    dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameter)));
                    dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonDataUpdate(objectMapper.writeValueAsString(feeParameterDTO)));
                    dataChangeDTO.setEntityId(feeParameter.getId().toString());
                    dataChangeService.createChangeActionEDIT(dataChangeDTO, FeeParameter.class);
                    totalDataSuccess++;
                }
            } catch (Exception e) {
                handleGeneralError(feeParameterDTO, e, validationErrors, errorMessageDTOList);
                totalDataFailed++;
            }
        }
        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }


    @Override
    public FeeParameterResponse updateSingleApprove(FeeParameterApproveRequest approveRequest, String clientIP) {
        log.info("Approve when update fee parameter with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeParameterDTO feeParameterDTO = null;

        try {
            /* validate data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* get data change by id and get json data after data */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());
            FeeParameterDTO dto = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), FeeParameterDTO.class);
            log.info("[Update Approve] Map data from JSON data after data change: {}", dto);

            /* get data by id */
            FeeParameter feeParameter = feeParameterRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            feeParameterMapper.mapObjectsDtoToEntity(dto, feeParameter);
            log.info("[Update Approve] Map object dto to entity: {}", feeParameter);

            feeParameterDTO = feeParameterMapper.mapToDto(feeParameter);
            log.info("[Update Approve] Map from entity to dto: {}", feeParameterDTO);

            /* check validation each column dto */
            Errors errors = validateFeeParameterUsingValidator(feeParameterDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set data change approve id and approve ip address */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setEntityId(feeParameter.getId().toString());

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                FeeParameter feeParameterUpdated = feeParameterMapper.updateEntity(feeParameter, dataChangeDTO);
                FeeParameter feeParameterSaved = feeParameterRepository.save(feeParameterUpdated);
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update data entity with id: " + feeParameterSaved.getId());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(feeParameterDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public List<FeeParameterDTO> getAll() {
        List<FeeParameter> all = feeParameterRepository.findAll();
        return feeParameterMapper.mapToDTOList(all);
    }

    @Override
    public FeeParameterDTO getByName(String name) {
        FeeParameter feeParameter = feeParameterRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Fee Parameter not found with name : " + name));
        return feeParameterMapper.mapToDto(feeParameter);
    }

    @Override
    public BigDecimal getValueByName(String name) {
        FeeParameter feeParameter = feeParameterRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Fee Parameter not found with name : " + name));
        return feeParameter.getFeeValue();
    }

    @Override
    public List<FeeParameterDTO> getByNameList(List<String> nameList) {
        List<FeeParameter> feeParameterList = feeParameterRepository.findFeeParameterByNameList(nameList);
        // Check if all names are present in the feeParameterList
        for (String name : nameList) {
            Optional<FeeParameter> foundParameter = feeParameterList.stream()
                    .filter(parameter -> parameter.getFeeName().equals(name))
                    .findFirst();

            if (foundParameter.isEmpty()) {
                // If a name is not found, you can throw a custom exception or handle it as needed
                throw new DataNotFoundException("FeeParameter with name '" + name + "' not found");
            }
        }

        return feeParameterMapper.mapToDTOList(feeParameterList);
    }

    @Override
    public Map<String, BigDecimal> getValueByNameList(List<String> nameList) {
        List<FeeParameter> feeParameterList = feeParameterRepository.findFeeParameterByNameList(nameList);

        Map<String, BigDecimal> dataMap = feeParameterList.stream()
                .collect(Collectors.toMap(FeeParameter::getFeeName, FeeParameter::getFeeValue));

        // Check if all names are present in the dataMap
        for (String name : nameList) {
            if (!dataMap.containsKey(name)) {
                // If a name is not found, you can throw a custom exception or handle it as needed
                throw new DataNotFoundException("FeeParameter with name '" + name + "' not found");
            }
        }

        return dataMap;
    }

    @Override
    public String deleteAll() {
        try {
            feeParameterRepository.deleteAll();
            return "Successfully deleted all Fee Parameter";
        } catch (Exception e) {
            log.error("Error when delete all Fee Parameter : {}", e.getMessage());
            throw new ConnectionDatabaseException("Error when delete all Fee Parameter");
        }
    }

    private void validationCodeAlreadyExists(String code, List<String> validationErrors) {
        if (isCodeAlreadyExists(code)) {
            validationErrors.add("Fee Parameter is already taken with code: " + code);
        }
    }

    private void validationNameAlreadyExists(String name, List<String> validationErrors) {
        if (isNameAlreadyExists(name)) {
            validationErrors.add("Fee Parameter is already taken with name: " + name);
        }
    }

    private Errors validateFeeParameterUsingValidator(FeeParameterDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "feeParameterDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void handleGeneralError(FeeParameterDTO feeParameterDTO, Exception e, List<String> validationErrors, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(feeParameterDTO != null ? feeParameterDTO.getFeeCode() : UNKNOWN, validationErrors));
    }

    private void validateDataChangeId(String dataChangeId) {
        if (dataChangeService.existById(Long.valueOf(dataChangeId))) {
            log.info("Data Change ids not found");
            throw new DataNotFoundException("Data Change ids not found");
        }
    }

    // Method to copy non-null and non-empty fields
    public void copyNonNullOrEmptyFields(FeeParameter feeParameter, FeeParameterDTO feeParameterDTO) {
        try {
            Map<String, String> entityProperties = BeanUtils.describe(feeParameter);

            for (Map.Entry<String, String> entry : entityProperties.entrySet()) {
                String propertyName = entry.getKey();
                String entityValue = entry.getValue();

                // Get the current value in the DTO
                String dtoValue = BeanUtils.getProperty(feeParameterDTO, propertyName);

                // Copy value from entity to DTO if DTO's value is null or empty
                if (isNullOrEmpty(dtoValue) && entityValue != null) {
                    BeanUtils.setProperty(feeParameterDTO, propertyName, entityValue);
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
