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
import com.bayu.billingservice.mapper.ModelMapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentManagementServiceImpl implements InvestmentManagementService {

    private final InvestmentManagementRepository investmentManagementRepository;
    private final BillingDataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final ModelMapperUtil modelMapperUtil;
    private final InvestmentManagementMapper investmentManagementMapper;

    private static final String ID_NOT_FOUND = "Investment Management not found with id: ";
    private static final String CODE_NOT_FOUND = "Investment Management not found with code: ";
    private static final String UNKNOWN = "unknown";

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return investmentManagementRepository.existsByCode(code);
    }

    @Override
    public CreateInvestmentManagementListResponse createSingleData(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data investment management with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromCreateRequestToDto(request);
        try {
            List<String> validationErrors = new ArrayList<>();
            validationCodeAlreadyExists(investmentManagementDTO.getCode(), validationErrors);

            if (validationErrors.isEmpty()) {
                dataChangeDTO.setInputId(request.getInputId());
                dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
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
        return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public CreateInvestmentManagementListResponse createMultipleData(CreateInvestmentManagementListRequest requestList, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create investment management list with request: {}", requestList);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (InvestmentManagementDTO investmentManagementDTO : requestList.getInvestmentManagementRequestList()) {
            try {
                List<String> validationErrors = new ArrayList<>();
                validationCodeAlreadyExists(investmentManagementDTO.getCode(), validationErrors);

                dataChangeDTO.setInputId(requestList.getInputId());
                dataChangeDTO.setInputIPAddress(requestList.getInputIPAddress());
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));

                if (validationErrors.isEmpty()) {
                    dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
                    totalDataSuccess++;
                } else {
                    ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(investmentManagementDTO.getCode(), validationErrors);
                    errorMessageList.add(errorMessageDTO);
                    totalDataFailed++;
                }
            } catch (Exception e) {
                handleGeneralError(investmentManagementDTO, e, errorMessageList);
                totalDataFailed++;
            }
        }
        return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public CreateInvestmentManagementListResponse createMultipleApprove(CreateInvestmentManagementListRequest requestList) {
        log.info("Create investment management list approve with request: {}", requestList);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeIds(requestList.getInvestmentManagementRequestList());
        for (InvestmentManagementDTO investmentManagementDTO : requestList.getInvestmentManagementRequestList()) {
            try {
                List<String> errorMessages = new ArrayList<>();

                validationCodeAlreadyExists(investmentManagementDTO.getCode(), errorMessages);

                Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));
                }

                BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(investmentManagementDTO.getDataChangeId());
                dataChangeDTO.setApproveId(requestList.getApproveId());
                dataChangeDTO.setApproveIPAddress(requestList.getApproveIPAddress());

                if (!errorMessages.isEmpty()) {
                    dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));
                    dataChangeService.approvalStatusIsRejected(dataChangeDTO, errorMessages);
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
                handleGeneralError(investmentManagementDTO, e, errorMessageList);
                totalDataFailed++;
            }
        }
        return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public UpdateInvestmentManagementListResponse updateSingleData(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update investment management by id with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        InvestmentManagementDTO investmentManagementDTO = investmentManagementMapper.mapFromUpdateRequestToDto(request);
        try {
            InvestmentManagement investmentManagement = investmentManagementRepository.findById(investmentManagementDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

            dataChangeDTO.setInputId(request.getInputId());
            dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
            dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));

            dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(investmentManagementDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public UpdateInvestmentManagementListResponse updateMultipleData(UpdateInvestmentManagementListRequest requestList, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update multiple investment management with request: {}", requestList);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (InvestmentManagementDTO investmentManagementDTO : requestList.getInvestmentManagementRequestList()) {
            try {
                InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(investmentManagementDTO.getCode())
                        .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + investmentManagementDTO.getId()));

                dataChangeDTO.setInputId(requestList.getInputId());
                dataChangeDTO.setInputIPAddress(requestList.getInputIPAddress());
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagementDTO)));

                dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
                totalDataSuccess++;
            } catch (Exception e) {
                handleGeneralError(investmentManagementDTO, e, errorMessageList);
                totalDataFailed++;
            }
        }
        return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public UpdateInvestmentManagementListResponse updateMultipleApprove(UpdateInvestmentManagementListRequest requestList) {
        log.info("Request data update approve: {}", requestList);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeIds(requestList.getInvestmentManagementRequestList());
        for (InvestmentManagementDTO investmentManagementDTO : requestList.getInvestmentManagementRequestList()) {
            try {
                List<String> validationErrors = new ArrayList<>();

                InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(investmentManagementDTO.getCode())
                        .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + investmentManagementDTO.getCode()));

                // Disini sudah dilakukan copy data dari DTO ke Entity
                modelMapperUtil.mapObjects(investmentManagementDTO, investmentManagement);
                log.info("Investment Management after copy properties: {}", investmentManagement);

                Errors errors = validateInvestmentManagementUsingValidator(investmentManagementMapper.mapFromEntityToDto(investmentManagement));
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
                }

                // Retrieve and set billing data change
                BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(investmentManagementDTO.getDataChangeId());
                dataChangeDTO.setApproveId(requestList.getApproveId());
                dataChangeDTO.setApproveIPAddress(requestList.getApproveIPAddress());
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
        }
        return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public DeleteInvestmentManagementListResponse deleteSingleData(DeleteInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
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
        return new DeleteInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public DeleteInvestmentManagementListResponse deleteMultipleApprove(DeleteInvestmentManagementListRequest requestList) {
        log.info("Request data delete approve: {}", requestList);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();


        validateDataChangeIds(requestList.getInvestmentManagementDTOList()
                .stream()
                .map(x -> InvestmentManagementDTO.builder().dataChangeId(x.getDataChangeId()).build()).toList());

        for (DeleteInvestmentManagementDTO deleteInvestmentManagementDTO : requestList.getInvestmentManagementDTOList()) {
            InvestmentManagementDTO investmentManagementDTO = InvestmentManagementDTO.builder()
                    .dataChangeId(deleteInvestmentManagementDTO.getDataChangeId())
                    .id(deleteInvestmentManagementDTO.getId())
                    .build();
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(investmentManagementDTO.getDataChangeId());
            try {
                InvestmentManagement investmentManagement = investmentManagementRepository.findById(investmentManagementDTO.getId())
                        .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

                dataChangeDTO.setApproveId(requestList.getApproveId());
                dataChangeDTO.setApproveIPAddress(requestList.getApproveIPAddress());
                dataChangeDTO.setApproveDate(new Date());
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(investmentManagement)));
                dataChangeDTO.setDescription("Successfully approve data change and delete data entity");

                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                investmentManagementRepository.delete(investmentManagement);
                totalDataSuccess++;

            } catch (DataNotFoundException e) {
                handleDataNotFoundException(investmentManagementDTO, e, errorMessageList);
                dataChangeDTO.setApproveId(requestList.getApproveId());
                dataChangeDTO.setApproveIPAddress(requestList.getApproveIPAddress());
                dataChangeDTO.setApproveDate(new Date());
                List<String> validationErrors = new ArrayList<>();
                validationErrors.add(ID_NOT_FOUND + investmentManagementDTO.getId());

                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } catch (Exception e) {
                handleGeneralError(investmentManagementDTO, e, errorMessageList);
                totalDataFailed++;
            }
        }
        return new DeleteInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
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
        return investmentManagementMapper.mapFromEntityToDto(investmentManagement);
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

    private void validateDataChangeIds(List<InvestmentManagementDTO> investmentManagementDTOList) {
        List<Long> idDataChangeList = investmentManagementDTOList.stream()
                .map(InvestmentManagementDTO::getDataChangeId)
                .toList();

        if (!dataChangeService.areAllIdsExistInDatabase(idDataChangeList)) {
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
