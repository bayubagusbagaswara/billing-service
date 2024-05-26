package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.*;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.mapper.FeeScheduleMapper;
import com.bayu.billingservice.model.FeeSchedule;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.FeeScheduleRepository;
import com.bayu.billingservice.service.DataChangeService;
import com.bayu.billingservice.service.FeeScheduleService;
import com.bayu.billingservice.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeScheduleServiceImpl implements FeeScheduleService {

    private static final String ID_NOT_FOUND = "Fee Schedule not found with id: ";
    private static final String CODE_NOT_FOUND = "Fee Schedule not found with code: ";
    private static final String UNKNOWN = "unknown";

    private final FeeScheduleRepository feeScheduleRepository;
    private final DataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final FeeScheduleMapper feeScheduleMapper;

    @Override
    public FeeScheduleResponse createSingleData(CreateFeeScheduleRequest createRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data fee schedule with request: {}", createRequest);
        FeeScheduleDTO feeScheduleDTO = feeScheduleMapper.mapCreateRequestToDto(createRequest);
        dataChangeDTO.setInputId(createRequest.getInputId());
        dataChangeDTO.setInputIPAddress(createRequest.getInputIPAddress());
        return processFeeScheduleCreation(feeScheduleDTO, dataChangeDTO);
    }

    @Override
    public FeeScheduleResponse createSingleApprove(FeeScheduleApproveRequest approveRequest) {
        log.info("Approve when create investment management with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        FeeScheduleDTO feeScheduleDTO = approveRequest.getData();
        try {
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            List<String> errorMessages = new ArrayList<>();

            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);

            FeeScheduleDTO dto = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), FeeScheduleDTO.class);

            Errors errors = validateFeeScheduleUsingValidator(feeScheduleDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));
            }

            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());

            if (!errorMessages.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeScheduleDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, errorMessages);
                totalDataFailed++;
            } else {
                FeeSchedule feeSchedule = feeScheduleMapper.createEntity(dto, dataChangeDTO);
                FeeSchedule feeScheduleSaved = feeScheduleRepository.save(feeSchedule);

                dataChangeDTO.setDescription("Successfully approve data change and save data fee schedule");
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeScheduleSaved)));
                dataChangeDTO.setEntityId(feeScheduleSaved.getId().toString());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private FeeScheduleResponse processFeeScheduleCreation(FeeScheduleDTO feeScheduleDTO, BillingDataChangeDTO dataChangeDTO) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        try {
            dataChangeDTO.setInputId(dataChangeDTO.getInputId());
            dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
            dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeScheduleDTO)));

            dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, errorMessageList);
            totalDataFailed++;
        }

        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public FeeScheduleResponse updateSingleData(UpdateFeeScheduleRequest updateRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update single data fee schedule with request: {}", updateRequest);
        dataChangeDTO.setInputId(updateRequest.getInputId());
        dataChangeDTO.setInputIPAddress(updateRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        FeeScheduleDTO feeScheduleDTO = feeScheduleMapper.mapUpdateRequestToDto(updateRequest);
        try {
            FeeSchedule feeSchedule = feeScheduleRepository.findById(feeScheduleDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + feeScheduleDTO.getId()));

            AtomicInteger successCounter = new AtomicInteger(totalDataSuccess);
            AtomicInteger failedCounter = new AtomicInteger(totalDataFailed);

            processUpdateFeeSchedule(feeSchedule, feeScheduleDTO, dataChangeDTO, errorMessageList, successCounter, failedCounter);

            totalDataSuccess = successCounter.get();
            totalDataFailed = failedCounter.get();
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public FeeScheduleResponse updateMultipleData(FeeScheduleListRequest updateListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update multiple data fee schedule with request: {}", updateListRequest);
        dataChangeDTO.setInputId(updateListRequest.getInputId());
        dataChangeDTO.setInputIPAddress(updateListRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (FeeScheduleDTO feeScheduleDTO : updateListRequest.getFeeScheduleDTOList()) {
            try {
                FeeSchedule feeSchedule = feeScheduleRepository.findById(feeScheduleDTO.getId())
                        .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + feeScheduleDTO.getId()));

                AtomicInteger successCounter = new AtomicInteger(totalDataSuccess);
                AtomicInteger failedCounter = new AtomicInteger(totalDataFailed);

                processUpdateFeeSchedule(feeSchedule, feeScheduleDTO, dataChangeDTO, errorMessageList, successCounter, failedCounter);

                totalDataSuccess = successCounter.get();
                totalDataFailed = failedCounter.get();
            } catch (Exception e) {
                handleGeneralError(feeScheduleDTO, e, errorMessageList);
                totalDataFailed++;
            }
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public FeeScheduleResponse updateSingleApprove(FeeScheduleApproveRequest approveRequest) {
        log.info("Approve when update fee schedule with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        FeeScheduleDTO feeScheduleDTO = approveRequest.getData();
        try {
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            List<String> validationErrors = new ArrayList<>();

            Errors errors = validateFeeScheduleUsingValidator(feeScheduleDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            FeeSchedule feeSchedule = feeScheduleRepository.findById(feeScheduleDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + feeScheduleDTO.getId()));

            // Copy data from DTO to Entity
            feeScheduleMapper.mapObjectsDtoToEntity(feeScheduleDTO, feeSchedule);
            log.info("Fee Schedule after copy properties: {}", feeSchedule);

            // Retrieve and set billing data change
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setEntityId(feeSchedule.getId().toString());

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeScheduleDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                FeeSchedule feeScheduleUpdated = feeScheduleMapper.updateEntity(feeSchedule, dataChangeDTO);
                FeeSchedule feeScheduleSaved = feeScheduleRepository.save(feeScheduleUpdated);

                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeScheduleSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update data entity");
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private void processUpdateFeeSchedule(FeeSchedule feeSchedule,
                                          FeeScheduleDTO feeScheduleDTO,
                                          BillingDataChangeDTO dataChangeDTO,
                                          List<ErrorMessageDTO> errorMessageList,
                                          AtomicInteger successCounter,
                                          AtomicInteger failedCounter) {
        try {
            dataChangeDTO.setInputId(dataChangeDTO.getInputId());
            dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeSchedule)));
            dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeScheduleDTO)));
            dataChangeDTO.setEntityId(feeSchedule.getId().toString());

            dataChangeService.createChangeActionEDIT(dataChangeDTO, FeeSchedule.class);
            successCounter.incrementAndGet(); // Increment totalDataSuccess
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, errorMessageList);
            failedCounter.incrementAndGet(); // Increment totalDataFailed
        }
    }

    @Override
    public FeeScheduleResponse deleteSingleData(DeleteFeeScheduleRequest deleteRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Delete single fee schedule with request: {}", deleteRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        FeeScheduleDTO feeScheduleDTO = FeeScheduleDTO.builder()
                .id(deleteRequest.getId())
                .build();
        try {
            FeeSchedule feeSchedule = feeScheduleRepository.findById(feeScheduleDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + feeScheduleDTO.getId()));

            dataChangeDTO.setInputId(deleteRequest.getInputId());
            dataChangeDTO.setInputIPAddress(deleteRequest.getInputIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeSchedule)));
            dataChangeDTO.setJsonDataAfter("");
            dataChangeDTO.setEntityId(feeSchedule.getId().toString());

            dataChangeService.createChangeActionDELETE(dataChangeDTO, InvestmentManagement.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public FeeScheduleResponse deleteSingleApprove(FeeScheduleApproveRequest approveRequest) {
        log.info("Approve when delete fee schedule t with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());

        FeeScheduleDTO feeScheduleDTO = approveRequest.getData();
        BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(Long.valueOf(approveRequest.getDataChangeId()));
        try {
            FeeSchedule feeSchedule = feeScheduleRepository.findById(feeScheduleDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + feeScheduleDTO.getId()));

            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeSchedule)));
            dataChangeDTO.setDescription("Successfully approve data change and delete data entity");

            dataChangeService.approvalStatusIsApproved(dataChangeDTO);
            feeScheduleRepository.delete(feeSchedule);
            totalDataSuccess++;

        } catch (DataNotFoundException e) {
            handleDataNotFoundException(feeScheduleDTO, e, errorMessageList);
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setApproveDate(new Date());
            List<String> validationErrors = new ArrayList<>();
            validationErrors.add(ID_NOT_FOUND + feeScheduleDTO.getId());

            dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
            totalDataFailed++;
        } catch (Exception e) {
            handleGeneralError(feeScheduleDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new FeeScheduleResponse(totalDataSuccess, totalDataFailed, errorMessageList);
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

    private void handleDataNotFoundException(FeeScheduleDTO feeScheduleDTO, DataNotFoundException e, List<ErrorMessageDTO> errorMessageList) {
        log.error("Fee Schedule not found with id: {}", feeScheduleDTO != null ? feeScheduleDTO.getId() : UNKNOWN, e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(feeScheduleDTO!= null ? String.valueOf(feeScheduleDTO.getId()) : UNKNOWN, validationErrors));
    }

    private void handleGeneralError(FeeScheduleDTO feeScheduleDTO, Exception e, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add("An unexpected error occurred: " + e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(feeScheduleDTO != null ? String.valueOf(feeScheduleDTO.getId()) : UNKNOWN, validationErrors));
    }
}
