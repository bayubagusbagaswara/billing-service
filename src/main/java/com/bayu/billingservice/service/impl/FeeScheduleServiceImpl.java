package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.*;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.mapper.FeeScheduleMapper;
import com.bayu.billingservice.model.FeeSchedule;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.FeeScheduleRepository;
import com.bayu.billingservice.service.DataChangeService;
import com.bayu.billingservice.service.FeeScheduleService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeScheduleServiceImpl implements FeeScheduleService {

    private static final String ID_NOT_FOUND = "Fee Schedule not found with id: ";
    private static final String UNKNOWN = "unknown";

    private final FeeScheduleRepository feeScheduleRepository;
    private final DataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final FeeScheduleMapper feeScheduleMapper;

    @Override
    public FeeScheduleResponse createSingleData(CreateFeeScheduleRequest createRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data fee schedule with request: {}", createRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeScheduleDTO feeScheduleDTO = null;

        try {
            /* mapping data from request to dto */
            feeScheduleDTO = feeScheduleMapper.mapCreateRequestToDto(createRequest);

            /* validation for each colum dto */
            Errors errors = validateFeeScheduleUsingValidator(feeScheduleDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set data input id for data change */
            dataChangeDTO.setInputId(createRequest.getInputId());

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = getErrorMessageDTO(feeScheduleDTO, validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeScheduleDTO)));
                dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public FeeScheduleResponse createSingleApprove(FeeScheduleApproveRequest approveRequest, String clientIP) {
        log.info("Approve when create fee schedule with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeScheduleDTO feeScheduleDTO = null;

        try {
            /* validating data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* mapping from json data after to Fee Schedule dto */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            feeScheduleDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), FeeScheduleDTO.class);

            /* set data change for approve id and approve ip address */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);

            /* There is no validation check, because it is already performed when making data changes */
            FeeSchedule feeSchedule = feeScheduleMapper.createEntity(feeScheduleDTO, dataChangeDTO);
            feeScheduleRepository.save(feeSchedule);
            dataChangeDTO.setDescription("Successfully approve data change and save data fee schedule with id: " + feeSchedule.getId());
            dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeSchedule)));
            dataChangeDTO.setEntityId(feeSchedule.getId().toString());
            dataChangeService.approvalStatusIsApproved(dataChangeDTO);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public FeeScheduleResponse updateSingleData(UpdateFeeScheduleRequest updateRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update single data fee schedule with request: {}", updateRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeScheduleDTO clonedDTO = null;

        try {
            /* mapping data from request to dto */
            FeeScheduleDTO feeScheduleDTO = feeScheduleMapper.mapUpdateRequestToDto(updateRequest);
            clonedDTO = new FeeScheduleDTO();
            BeanUtil.copyAllProperties(feeScheduleDTO, clonedDTO);
            log.info("[Update Single] Result mapping request to dto: {}", feeScheduleDTO);

            /* get fee schedule by id */
            FeeSchedule feeSchedule = feeScheduleRepository.findById(feeScheduleDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + feeScheduleDTO.getId()));

            /* data yang akan di validator */
            copyNonNullOrEmptyFields(feeSchedule, clonedDTO);
            log.info("[Update Single] Result map object entity to dto: {}", clonedDTO);

            /* check validator for data request after mapping to dto */
            Errors errors = validateFeeScheduleUsingValidator(clonedDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set input id for data change */
            dataChangeDTO.setInputId(updateRequest.getInputId());

            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = getErrorMessageDTO(feeScheduleDTO, validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeSchedule)));
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonDataUpdate(objectMapper.writeValueAsString(feeScheduleDTO)));
                dataChangeDTO.setEntityId(feeSchedule.getId().toString());
                dataChangeService.createChangeActionEDIT(dataChangeDTO, FeeSchedule.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(clonedDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public FeeScheduleResponse updateSingleApprove(FeeScheduleApproveRequest approveRequest, String clientIP) {
        log.info("Approve when update fee schedule with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeScheduleDTO feeScheduleDTO = null;

        try {
            /* validating data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* get data change by id and get json data after */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());
            FeeScheduleDTO dto = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), FeeScheduleDTO.class);
            log.info("[Update Approve] Map data from JSON data after data change: {}", dto);

            /* get entity data by id */
            FeeSchedule feeSchedule = feeScheduleRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            feeScheduleMapper.mapObjectsDtoToEntity(dto, feeSchedule);
            log.info("[Update Approve] Map object dto to entity: {}", feeSchedule);

            feeScheduleDTO = feeScheduleMapper.mapToDto(feeSchedule);
            log.info("[Update Approve] Map object from entity to dto: {}", feeScheduleDTO);

            /* check validation for each column dto */
            Errors errors = validateFeeScheduleUsingValidator(feeScheduleDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set data change approve id and approve ip address */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setEntityId(feeSchedule.getId().toString());

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeScheduleDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                FeeSchedule feeScheduleUpdated = feeScheduleMapper.updateEntity(feeSchedule, dataChangeDTO);
                FeeSchedule feeScheduleSaved = feeScheduleRepository.save(feeScheduleUpdated);
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeScheduleSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update data entity with id: " + feeScheduleSaved.getId());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public FeeScheduleResponse deleteSingleData(DeleteFeeScheduleRequest deleteRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Delete single fee schedule with request: {}", deleteRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeScheduleDTO feeScheduleDTO = null;

        try {
            /* get data by id */
            Long id = deleteRequest.getId();
            FeeSchedule feeSchedule = feeScheduleRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + id));

            feeScheduleDTO = feeScheduleMapper.mapToDto(feeSchedule);

            dataChangeDTO.setInputId(deleteRequest.getInputId());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeSchedule)));
            dataChangeDTO.setJsonDataAfter("");
            dataChangeDTO.setEntityId(feeSchedule.getId().toString());
            dataChangeService.createChangeActionDELETE(dataChangeDTO, InvestmentManagement.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public FeeScheduleResponse deleteSingleApprove(FeeScheduleApproveRequest approveRequest, String clientIP) {
        log.info("Approve when delete fee schedule with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        FeeScheduleDTO feeScheduleDTO = null;

        try {
            /* validating data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* get data change by id and get entity Id */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());

            /* get entity by id */
            FeeSchedule feeSchedule = feeScheduleRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            /* mapping from entity to dto */
            feeScheduleDTO = feeScheduleMapper.mapToDto(feeSchedule);

            /* set data change for approve id and approve ip address */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeSchedule)));
            dataChangeDTO.setDescription("Successfully approve data change and delete data entity with id: " + feeSchedule.getId());
            dataChangeService.approvalStatusIsApproved(dataChangeDTO);

            /* delete data entity in the database */
            feeScheduleRepository.delete(feeSchedule);
            totalDataSuccess++;

        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public BigDecimal checkFeeScheduleAndGetFeeValue(BigDecimal amount) {
        return feeScheduleRepository.checkFeeScheduleAndGetFeeValue(amount);
    }

    @Override
    public String deleteAll() {
        feeScheduleRepository.deleteAll();
        return "Successfully delete all Fee Schedule";
    }

    @Override
    public List<FeeScheduleDTO> getAll() {
        List<FeeSchedule> all = feeScheduleRepository.findAll();
        return feeScheduleMapper.mapToDTOList(all);
    }

    public Errors validateFeeScheduleUsingValidator(FeeScheduleDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "feeScheduleDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void validateDataChangeId(String dataChangeId) {
        if (dataChangeService.existById(Long.valueOf(dataChangeId))) {
            log.info("Data Change not found with id: {}", dataChangeId);
            throw new DataNotFoundException("Data Change id not found with id: " + dataChangeId);
        }
    }

    private void handleGeneralError(FeeScheduleDTO feeScheduleDTO, Exception e, List<String> validationErrors, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(feeScheduleDTO != null ? String.valueOf(feeScheduleDTO.getId()) : UNKNOWN, validationErrors));
    }

    private static ErrorMessageDTO getErrorMessageDTO(FeeScheduleDTO feeScheduleDTO, List<String> validationErrors) {
        String string = feeScheduleDTO.getId() == null ? UNKNOWN : feeScheduleDTO.getId().toString();
        return new ErrorMessageDTO(string, validationErrors);
    }

    public void copyNonNullOrEmptyFields(FeeSchedule feeSchedule, FeeScheduleDTO feeScheduleDTO) {
        try {
            Map<String, String> entityProperties = BeanUtils.describe(feeSchedule);

            for (Map.Entry<String, String> entry : entityProperties.entrySet()) {
                String propertyName = entry.getKey();
                String entityValue = entry.getValue();

                String dtoValue = BeanUtils.getProperty(feeScheduleDTO, propertyName);

                if (isNullOrEmpty(dtoValue) && entityValue != null) {
                    BeanUtils.setProperty(feeScheduleDTO, propertyName, entityValue);
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
