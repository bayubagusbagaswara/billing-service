package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.mapper.InvestmentManagementMapper;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.InvestmentManagementRepository;
import com.bayu.billingservice.service.DataChangeService;
import com.bayu.billingservice.service.InvestmentManagementService;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentManagementServiceImpl implements InvestmentManagementService {

    private static final String ID_NOT_FOUND = "Investment Management not found with id: ";
    private static final String CODE_NOT_FOUND = "Investment Management not found with code: ";
    private static final String UNKNOWN = "unknown";

    private final InvestmentManagementRepository investmentManagementRepository;
    private final DataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final InvestmentManagementMapper investmentManagementMapper;

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return investmentManagementRepository.existsByCode(code);
    }

    @Override
    public InvestmentManagementResponse createSingleData(CreateInvestmentManagementRequest createRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data investment management with request: {}", createRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        try {
            List<String> validationErrors = new ArrayList<>();

            /* mapping data from request to dto */
            InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromCreateRequestToDto(createRequest);

            /* validation for each column dto */
            Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error-> validationErrors.add(error.getDefaultMessage()));
            }

            /* validation code already exists */
            validationCodeAlreadyExists(investmentManagementDTO.getCode(), validationErrors);

            /* set data input id to data change */
            dataChangeDTO.setInputId(createRequest.getInputId());

            /* check validation errors for customer response */
            if (validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
                totalDataSuccess++;
            } else {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(investmentManagementDTO.getCode(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            }
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageDTOList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public InvestmentManagementResponse createMultipleData(CreateInvestmentManagementListRequest createListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create multiple investment management with request: {}", createListRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        /* repeat data one by one */
        for (CreateInvestmentManagementDataListRequest createInvestmentManagementDataListRequest : createListRequest.getCreateInvestmentManagementDataListRequests()) {
            /* mapping data from request to dto */
            InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromDataListToDTO(createInvestmentManagementDataListRequest);
            log.info("[Create Multiple] Result mapping request to dto: {}", investmentManagementDTO);
            try {
                List<String> validationErrors = new ArrayList<>();
                /* validation for each column dto */
                Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error-> validationErrors.add(error.getDefaultMessage()));
                }
                /* validation code already exists */
                validationCodeAlreadyExists(investmentManagementDTO.getCode(), validationErrors);

                /* set data input id to data change */
                dataChangeDTO.setInputId(createListRequest.getInputId());

                /* check validation error */
                if (validationErrors.isEmpty()) {
                    dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                    dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
                    totalDataSuccess++;
                } else {
                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(investmentManagementDTO.getCode(), validationErrors);
                    errorMessageDTOList.add(errorMessageDTO);
                    totalDataFailed++;
                }
            } catch (Exception e) {
                handleGeneralError(null, e, errorMessageDTOList);
                totalDataFailed++;
            }
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public InvestmentManagementResponse createSingleApprove(InvestmentManagementApproveRequest approveRequest, String approveIPAddress) {
        log.info("Approve when create investment management with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        try {
            /* mapping from data JSON DATA After to class dto InvestmentManagement */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            InvestmentManagementDTO investmentManagementDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), InvestmentManagementDTO.class);

            /* check validation code already exists */
            List<String> validationErrors = new ArrayList<>();
            validationCodeAlreadyExists(investmentManagementDTO.getCode(), validationErrors);

            /* set data change for approveId and approveIPAddress */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveIPAddress);

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                InvestmentManagement investmentManagement = investmentManagementMapper.createEntity(investmentManagementDTO, dataChangeDTO);
                investmentManagementRepository.save(investmentManagement);
                dataChangeDTO.setDescription("Successfully approve data change and save data investment management with id: " + investmentManagement.getId());
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
                dataChangeDTO.setEntityId(investmentManagement.getId().toString());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public InvestmentManagementResponse updateSingleData(UpdateInvestmentManagementRequest updateRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update single data investment management with request: {}", updateRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        try {
            /* mapping data from request to dto */
            InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromUpdateRequestToDto(updateRequest);
            InvestmentManagementDTO clonedDTO = new InvestmentManagementDTO();
            BeanUtil.copyAllProperties(investmentManagementDTO, clonedDTO);
            log.info("[Update Single] Result mapping request to dto: {}", investmentManagementDTO);

            /* get investment management by id */
            InvestmentManagement investmentManagement = investmentManagementRepository.findById(investmentManagementDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

            /* data yang akan di validator */
            copyNonNullOrEmptyFields(investmentManagement, clonedDTO);
            log.info("[Update Single] Result map object entity to dto: {}", clonedDTO);

            /* check validator for data request after mapping to dto */
            List<String> validationErrors = new ArrayList<>();
            Errors errors = validateInvestmentManagementUsingValidator(clonedDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(investmentManagementDTO.getCode(), validationErrors);
                errorMessageList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setInputId(updateRequest.getInputId());
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonDataUpdate(objectMapper.writeValueAsString(investmentManagementDTO)));
                dataChangeDTO.setEntityId(investmentManagement.getId().toString());
                dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(updateRequest.getId().toString(), e, errorMessageList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public InvestmentManagementResponse updateMultipleData(UpdateInvestmentManagementListRequest updateListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update multiple data investment management with request: {}", updateListRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        /* repeat data one by one */
        for (UpdateInvestmentManagementDataListRequest updateInvestmentManagementDataListRequest : updateListRequest.getUpdateInvestmentManagementDataListRequests()) {
            /* mapping data from request to dto */
            InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromDataListToDTO(updateInvestmentManagementDataListRequest);
            log.info("[Update Multiple] Result mapping from request to dto: {}", investmentManagementDTO);
            try {
                /* get data by code */
                InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(investmentManagementDTO.getCode())
                        .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + investmentManagementDTO.getCode()));

                /* map data from dto to entity, to overwrite new data */
                investmentManagementMapper.mapObjectsDtoToEntity(investmentManagementDTO, investmentManagement);
                log.info("[Update Multiple] Result map object dto to entity: {}", investmentManagement); // disini sudah gabung semua
                InvestmentManagementDTO dto = investmentManagementMapper.mapToDto(investmentManagement);
                log.info("[Update Multiple] Result map object entity to dto: {}", dto);

                /* check validation data dto */
                List<String> validationErrors = new ArrayList<>();
                Errors errors = validateInvestmentManagementUsingValidator(dto);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
                }

                if (!validationErrors.isEmpty()) {
                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(investmentManagementDTO.getCode(), validationErrors);
                    errorMessageList.add(errorMessageDTO);
                    totalDataFailed++;
                } else {
                    dataChangeDTO.setInputId(updateListRequest.getInputId());
                    dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
                    dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonDataUpdate(objectMapper.writeValueAsString(investmentManagementDTO)));
                    dataChangeDTO.setEntityId(investmentManagement.getId().toString());
                    dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
                   totalDataSuccess++;
                }
            } catch (Exception e) {
                handleGeneralError(null, e, errorMessageList);
                totalDataFailed++;
            }
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }


    @Override
    public InvestmentManagementResponse updateSingleApprove(InvestmentManagementApproveRequest approveRequest, String approveIPAddress) {
        log.info("Approve when update investment management with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        try {
            /* get data change by id and get json data after data */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            InvestmentManagementDTO investmentManagementDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), InvestmentManagementDTO.class);
            log.info("[Update Approve] Map data from JSON data after data change: {}", investmentManagementDTO);

            /* get data by code*/
            InvestmentManagement investmentManagement = investmentManagementRepository.findById(Long.valueOf(dataChangeDTO.getEntityId()))
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + dataChangeDTO.getEntityId()));

            investmentManagementMapper.mapObjectsDtoToEntity(investmentManagementDTO, investmentManagement);
            log.info("[Update Approve] Map object dto to entity: {}", investmentManagement);

            InvestmentManagementDTO dto = investmentManagementMapper.mapToDto(investmentManagement);
            log.info("[Update Approve] Map from entity to dto: {}", dto);

            /* check validation each column */
            List<String> validationErrors = new ArrayList<>();
            Errors errors = validateInvestmentManagementUsingValidator(dto);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set data change information */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveIPAddress);
            dataChangeDTO.setEntityId(investmentManagement.getId().toString());

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                InvestmentManagement investmentManagementUpdated = investmentManagementMapper.updateEntity(investmentManagement, dataChangeDTO);
                InvestmentManagement investmentManagementSaved = investmentManagementRepository.save(investmentManagementUpdated);
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update data entity with id: " + investmentManagementSaved.getId());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public InvestmentManagementResponse deleteSingleData(DeleteInvestmentManagementRequest deleteRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Delete single investment management with request: {}", deleteRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        try {
            /* get data by id */
            Long id = deleteRequest.getId();
            InvestmentManagement investmentManagement= investmentManagementRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + id));

            dataChangeDTO.setInputId(deleteRequest.getInputId());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
            dataChangeDTO.setJsonDataAfter("");
            dataChangeDTO.setEntityId(investmentManagement.getId().toString());
            dataChangeService.createChangeActionDELETE(dataChangeDTO, InvestmentManagement.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(deleteRequest.getId().toString(), e, errorMessageList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public InvestmentManagementResponse deleteSingleApprove(InvestmentManagementApproveRequest approveRequest, String approveIPAddress) {
        log.info("Approve when delete investment management with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        try {
            /* get data change by id and get Entity ID */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());

            InvestmentManagement investmentManagement = investmentManagementRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveIPAddress);
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
            dataChangeDTO.setDescription("Successfully approve data change and delete data entity with id: " + investmentManagement.getId());
            dataChangeService.approvalStatusIsApproved(dataChangeDTO);
            investmentManagementRepository.delete(investmentManagement);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(null, e, errorMessageList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public String deleteAll() {
        investmentManagementRepository.deleteAll();
        return "Successfully delete all investment management";
    }

    @Override
    public List<InvestmentManagementDTO> getAll() {
        List<InvestmentManagement> all = investmentManagementRepository.findAll();
        return investmentManagementMapper.mapToDTOList(all);
    }

    @Override
    public InvestmentManagementDTO getByCode(String investmentManagementCode) {
        InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(investmentManagementCode)
                .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + investmentManagementCode));
        return investmentManagementMapper.mapToDto(investmentManagement);
    }

    public Errors validateInvestmentManagementUsingValidator(InvestmentManagementDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "investmentManagementDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void validationCodeAlreadyExists(String code, List<String> errorMessages) {
        if (isCodeAlreadyExists(code)) {
            errorMessages.add("Investment Management is already taken with code: " + code);
        }
    }

    private void validateDataChangeId(String dataChangeId) {
        if (!dataChangeService.existById(Long.valueOf(dataChangeId))) {
            log.info("Data Change ids not found");
            throw new DataNotFoundException("Data Change ids not found");
        }
    }

    private void handleGeneralError(String investmentManagementCode, Exception e, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(investmentManagementCode != null ? investmentManagementCode : UNKNOWN, validationErrors));
    }

    // Method to copy non-null and non-empty fields
    public void copyNonNullOrEmptyFields(InvestmentManagement investmentManagement, InvestmentManagementDTO investmentManagementDTO) {
        try {
            Map<String, String> entityProperties = BeanUtils.describe(investmentManagement);

            for (Map.Entry<String, String> entry : entityProperties.entrySet()) {
                String propertyName = entry.getKey();
                String entityValue = entry.getValue();

                // Get the current value in the DTO
                String dtoValue = BeanUtils.getProperty(investmentManagementDTO, propertyName);

                // Copy value from entity to DTO if DTO's value is null or empty
                if (isNullOrEmpty(dtoValue) && entityValue != null) {
                    BeanUtils.setProperty(investmentManagementDTO, propertyName, entityValue);
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
