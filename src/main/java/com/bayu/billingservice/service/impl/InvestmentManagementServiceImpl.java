package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.mapper.InvestmentManagementMapper;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.InvestmentManagementRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.service.InvestmentManagementService;
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

    private final InvestmentManagementRepository investmentManagementRepository;
    private final BillingDataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final InvestmentManagementMapper investmentManagementMapper;

    private static final String ID_NOT_FOUND = "Investment Management not found with id: ";
    private static final String CODE_NOT_FOUND = "Investment Management not found with code: ";
    private static final String UNKNOWN = "unknown";

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return investmentManagementRepository.existsByCode(code);
    }

    @Override
    public InvestmentManagementResponse createSingleData(CreateInvestmentManagementRequest createInvestmentManagementRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data investment management with request: {}", createInvestmentManagementRequest);
        InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromCreateRequestToDto(createInvestmentManagementRequest);
        dataChangeDTO.setInputId(createInvestmentManagementRequest.getInputId());
        dataChangeDTO.setInputIPAddress(createInvestmentManagementRequest.getInputIPAddress());
        return processInvestmentManagementCreation(investmentManagementDTO, dataChangeDTO);
    }

    @Override
    public InvestmentManagementResponse createMultipleData(InvestmentManagementListRequest investmentManagementListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create investment management list with request: {}", investmentManagementListRequest);
        dataChangeDTO.setInputId(investmentManagementListRequest.getInputId());
        dataChangeDTO.setInputIPAddress(investmentManagementListRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (InvestmentManagementDTO investmentManagementDTO : investmentManagementListRequest.getInvestmentManagementDTOList()) {
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
            validationCodeAlreadyExists(investmentManagementDTO.getCode(), validationErrors);

            dataChangeDTO.setInputId(dataChangeDTO.getInputId());
            dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
            dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));

            if (validationErrors.isEmpty()) {
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
        log.info("Create investment management list approve with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        InvestmentManagementDTO investmentManagementDTO = approveRequest.getData();
        try {
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            List<String> errorMessages = new ArrayList<>();

            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);

            InvestmentManagementDTO dto = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), InvestmentManagementDTO.class);
            validationCodeAlreadyExists(investmentManagementDTO.getCode(), errorMessages);

            Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));
            }


            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());

            if (!errorMessages.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, errorMessages);
                totalDataFailed++;
            } else {
                InvestmentManagement investmentManagement = investmentManagementMapper.createEntity(dto, dataChangeDTO);
                investmentManagementRepository.save(investmentManagement);

                dataChangeDTO.setDescription("Successfully approve data change and save data investment management");
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
                dataChangeDTO.setEntityId(investmentManagement.getId().toString());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
                handleGeneralError(investmentManagementDTO, e, errorMessageList);
                totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public InvestmentManagementResponse updateSingleData(UpdateInvestmentManagementRequest updateInvestmentManagementRequest, BillingDataChangeDTO dataChangeDTO) {
        dataChangeDTO.setInputId(updateInvestmentManagementRequest.getInputId());
        dataChangeDTO.setInputIPAddress(updateInvestmentManagementRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromUpdateRequestToDto(updateInvestmentManagementRequest);
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
    public InvestmentManagementResponse updateMultipleData(InvestmentManagementListRequest updateInvestmentManagementListRequest, BillingDataChangeDTO dataChangeDTO) {
        dataChangeDTO.setInputId(updateInvestmentManagementListRequest.getInputId());
        dataChangeDTO.setInputIPAddress(updateInvestmentManagementListRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (InvestmentManagementDTO investmentManagementDTO : updateInvestmentManagementListRequest.getInvestmentManagementDTOList()) {
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
            dataChangeDTO.setInputId(dataChangeDTO.getInputId());
            dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
            dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
            dataChangeDTO.setEntityId(investmentManagement.getId().toString());

            dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
            successCounter.incrementAndGet(); // Increment totalDataSuccess
        } catch (Exception e) {
            handleGeneralError(investmentManagementDTO, e, errorMessageList);
            failedCounter.incrementAndGet(); // Increment totalDataFailed
        }
    }

    @Override
    public InvestmentManagementResponse updateSingleApprove(InvestmentManagementApproveRequest approveRequest) {
        log.info("Request data update approve: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        InvestmentManagementDTO investmentManagementDTO = approveRequest.getData();
        try {
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            List<String> validationErrors = new ArrayList<>();

            Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(investmentManagementDTO.getCode())
                    .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + investmentManagementDTO.getCode()));

            // Disini sudah dilakukan copy data dari DTO ke Entity
            investmentManagementMapper.mapObjects(investmentManagementDTO, investmentManagement);
            log.info("Investment Management after copy properties: {}", investmentManagement);



            // Retrieve and set billing data change
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setEntityId(investmentManagement.getId().toString());

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                InvestmentManagement investmentManagementUpdated = investmentManagementMapper.updateEntity(investmentManagement, dataChangeDTO);
                // Coba di test apakah terjadi duplikat data. Harusnya hanya akan update data sebelumnya dengan id yang sama
                InvestmentManagement investmentManagementSaved = investmentManagementRepository.save(investmentManagementUpdated);

                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update data entity");
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(investmentManagementDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new InvestmentManagementResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public InvestmentManagementResponse deleteSingleData(DeleteInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Delete investment management by id with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        InvestmentManagementDTO investmentManagementDTO = InvestmentManagementDTO.builder()
                .id(request.getId())
                .build();
        try {
            InvestmentManagement investmentManagement= investmentManagementRepository.findById(investmentManagementDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

            dataChangeDTO.setInputId(request.getInputId());
            dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
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
        log.info("Request data delete approve: {}", approveRequest);
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
