package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.mapper.InvestmentManagementMapper;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.InvestmentManagementRepository;
import com.bayu.billingservice.service.DataChangeService;
import com.bayu.billingservice.service.InvestmentManagementService;
import com.bayu.billingservice.util.EmailValidator;
import com.bayu.billingservice.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final EmailValidator emailValidator;

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return investmentManagementRepository.existsByCode(code);
    }

    @Override
    public InvestmentManagementResponse createSingleData(CreateInvestmentManagementRequest createRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data investment management with request: {}", createRequest);
        InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromCreateRequestToDto(createRequest);
        dataChangeDTO.setInputId(createRequest.getInputId());
        dataChangeDTO.setInputIPAddress(createRequest.getInputIPAddress());
        return processInvestmentManagementCreation(investmentManagementDTO, dataChangeDTO);
    }

    @Override
    public InvestmentManagementResponse createMultipleData(CreateInvestmentManagementListRequest createListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create multiple investment management with request: {}", createListRequest);
        dataChangeDTO.setInputId(createListRequest.getInputId());
        dataChangeDTO.setInputIPAddress(createListRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (CreateInvestmentManagementDataListRequest createInvestmentManagementDataListRequest : createListRequest.getCreateInvestmentManagementDataListRequests()) {
            InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromDataListToDTO(createInvestmentManagementDataListRequest);
            InvestmentManagementResponse response = processInvestmentManagementCreation(investmentManagementDTO, dataChangeDTO);
            totalDataSuccess += response.getTotalDataSuccess();
            totalDataFailed += response.getTotalDataFailed();
            errorMessageList.addAll(response.getErrorMessageDTOList());
        }

        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private InvestmentManagementResponse processInvestmentManagementCreation(InvestmentManagementDTO investmentManagementDTO, BillingDataChangeDTO dataChangeDTO) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        try {
            List<String> validationErrors = new ArrayList<>();

            // validation column dto
            Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error-> validationErrors.add(error.getDefaultMessage()));
            }

            // validation code already exists
            validationCodeAlreadyExists(investmentManagementDTO.getCode(), validationErrors);

            if (validationErrors.isEmpty()) {
                dataChangeDTO.setInputId(dataChangeDTO.getInputId());
                dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));

                dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
                totalDataSuccess++;
            } else {
                totalDataFailed++;
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(investmentManagementDTO.getCode(), validationErrors);
                errorMessageList.add(errorMessageDTO);
            }
        } catch (Exception e) {
            handleGeneralError(investmentManagementDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public InvestmentManagementResponse createSingleApprove(InvestmentManagementApproveRequest approveRequest) {
        log.info("Approve when create investment management with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        try {
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            List<String> validationErrors = new ArrayList<>();

            // Mapping from data JSON DATA After to class dto InvestmentManagement
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            InvestmentManagementDTO investmentManagementDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), InvestmentManagementDTO.class);

            // check validation
            validationCodeAlreadyExists(investmentManagementDTO.getCode(), validationErrors);

            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                InvestmentManagement investmentManagement = investmentManagementMapper.createEntity(investmentManagementDTO, dataChangeDTO);
                investmentManagementRepository.save(investmentManagement);

                dataChangeDTO.setDescription("Successfully approve data change and save data investment management");
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
        dataChangeDTO.setInputId(updateRequest.getInputId());
        dataChangeDTO.setInputIPAddress(updateRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromUpdateRequestToDto(updateRequest);
        try {
            InvestmentManagement investmentManagement = investmentManagementRepository.findById(investmentManagementDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

            AtomicInteger successCounter = new AtomicInteger(totalDataSuccess);
            AtomicInteger failedCounter = new AtomicInteger(totalDataFailed);

            processUpdateInvestmentManagement(investmentManagement, investmentManagementDTO, dataChangeDTO, errorMessageList, successCounter, failedCounter);

            totalDataSuccess = successCounter.get();
            totalDataFailed = failedCounter.get();
        } catch (Exception e) {
            handleGeneralError(investmentManagementDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public InvestmentManagementResponse updateMultipleData(UpdateInvestmentManagementListRequest updateListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update multiple data investment management with request: {}", updateListRequest);
        dataChangeDTO.setInputId(updateListRequest.getInputId());
        dataChangeDTO.setInputIPAddress(updateListRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (UpdateInvestmentManagementDataListRequest updateInvestmentManagementDataListRequest : updateListRequest.getUpdateInvestmentManagementDataListRequests()) {
            InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromDataListToDTO(updateInvestmentManagementDataListRequest);
            try {
                InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(investmentManagementDTO.getCode())
                        .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + investmentManagementDTO.getCode()));

                AtomicInteger successCounter = new AtomicInteger(totalDataSuccess);
                AtomicInteger failedCounter = new AtomicInteger(totalDataFailed);

                processUpdateInvestmentManagement(investmentManagement, investmentManagementDTO, dataChangeDTO, errorMessageList, successCounter, failedCounter);

                totalDataSuccess = successCounter.get();
                totalDataFailed = failedCounter.get();
            } catch (Exception e) {
                handleGeneralError(investmentManagementDTO, e, errorMessageList);
                totalDataFailed++;
            }
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private void processUpdateInvestmentManagement(InvestmentManagement investmentManagement,
                                                   InvestmentManagementDTO investmentManagementDTO,
                                                   BillingDataChangeDTO dataChangeDTO,
                                                   List<ErrorMessageDTO> errorMessageList,
                                                   AtomicInteger successCounter,
                                                   AtomicInteger failedCounter) {
        try {
            List<String> validationErrors = new ArrayList<>();
            validationEmail(investmentManagementDTO.getEmail(), validationErrors);

            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(investmentManagementDTO.getCode(), validationErrors);
                errorMessageList.add(errorMessageDTO);
                failedCounter.incrementAndGet();
            } else {
                dataChangeDTO.setInputId(dataChangeDTO.getInputId());
                dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                dataChangeDTO.setEntityId(investmentManagement.getId().toString());

                dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
                successCounter.incrementAndGet(); // Increment totalDataSuccess
            }
        } catch (Exception e) {
            handleGeneralError(investmentManagementDTO, e, errorMessageList);
            failedCounter.incrementAndGet(); // Increment totalDataFailed
        }
    }

    @Override
    public InvestmentManagementResponse updateSingleApprove(InvestmentManagementApproveRequest approveRequest) {
        log.info("Approve when update investment management with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        // InvestmentManagementDTO investmentManagementDTO = approveRequest.getData(); // data ini gak digunakan, karena kita ambil saja data dari JSON After data change
        try {
            List<String> validationErrors = new ArrayList<>();
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());

            // JANGAN LAKUKAN VALIDATION COLUMN DTO PADA SAAT APPROVE
            // Karena validation column harusnya dilakukan saat insert data change
            // Mapping from data JSON DATA After to class dto InvestmentManagement
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);

            // kita dapat object dto dari JSON Data after
            InvestmentManagementDTO investmentManagementDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), InvestmentManagementDTO.class);
            log.info("Data dari json after: {}", investmentManagementDTO);

            // kita get entity
            InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(investmentManagementDTO.getCode())
                    .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + investmentManagementDTO.getCode()));

            // Copy data from DTO to Entity
            investmentManagementMapper.mapObjects(investmentManagementDTO, investmentManagement);
            log.info("Investment Management after copy properties: {}", investmentManagement);

            validationEmail(investmentManagement.getEmail(), validationErrors);

            // Retrieve and set billing data change
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setEntityId(investmentManagement.getId().toString());

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                InvestmentManagement investmentManagementUpdated = investmentManagementMapper.updateEntity(investmentManagement, dataChangeDTO);
                InvestmentManagement investmentManagementSaved = investmentManagementRepository.save(investmentManagementUpdated);

                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update data entity");
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

        InvestmentManagementDTO investmentManagementDTO = InvestmentManagementDTO.builder()
                .id(deleteRequest.getId())
                .build();
        try {
            InvestmentManagement investmentManagement= investmentManagementRepository.findById(investmentManagementDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

            dataChangeDTO.setInputId(deleteRequest.getInputId());
            dataChangeDTO.setInputIPAddress(deleteRequest.getInputIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
            dataChangeDTO.setJsonDataAfter("");
            dataChangeDTO.setEntityId(investmentManagement.getId().toString());

            dataChangeService.createChangeActionDELETE(dataChangeDTO, InvestmentManagement.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(investmentManagementDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public InvestmentManagementResponse deleteSingleApprove(InvestmentManagementApproveRequest approveRequest) {
        log.info("Approve when delete investment management with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());

        InvestmentManagementDTO investmentManagementDTO = approveRequest.getData();
        BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(Long.valueOf(approveRequest.getDataChangeId()));
        try {
            InvestmentManagement investmentManagement = investmentManagementRepository.findById(investmentManagementDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
            dataChangeDTO.setDescription("Successfully approve data change and delete data entity");

            dataChangeService.approvalStatusIsApproved(dataChangeDTO);
            investmentManagementRepository.delete(investmentManagement);
            totalDataSuccess++;

        } catch (DataNotFoundException e) {
            handleDataNotFoundException(investmentManagementDTO, e, errorMessageList);
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setApproveDate(new Date());
            List<String> validationErrors = new ArrayList<>();
            validationErrors.add(ID_NOT_FOUND + investmentManagementDTO.getId());

            dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
            totalDataFailed++;
        } catch (Exception e) {
            handleGeneralError(investmentManagementDTO, e, errorMessageList);
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


    private void validationEmail(String email, List<String> validationErrors) {
        if (!emailValidator.isValidEmail(email)) {
            validationErrors.add("Email is not valid: " + email);
        }
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

    private void handleDataNotFoundException(InvestmentManagementDTO investmentManagementDTO, DataNotFoundException e, List<ErrorMessageDTO> errorMessageList) {
        log.error("Investment Management not found with id: {}", investmentManagementDTO != null ? investmentManagementDTO.getCode() : UNKNOWN, e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(investmentManagementDTO != null ? investmentManagementDTO.getCode() : UNKNOWN, validationErrors));
    }

    private void handleGeneralError(InvestmentManagementDTO investmentManagementDTO, Exception e, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add("An unexpected error occurred: " + e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(investmentManagementDTO != null ? investmentManagementDTO.getCode() : UNKNOWN, validationErrors));
    }

}
