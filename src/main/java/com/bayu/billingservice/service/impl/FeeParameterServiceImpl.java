package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeparameter.*;
import com.bayu.billingservice.exception.ConnectionDatabaseException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.mapper.FeeParameterMapper;
import com.bayu.billingservice.model.FeeParameter;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.FeeParameterRepository;
import com.bayu.billingservice.service.DataChangeService;
import com.bayu.billingservice.service.FeeParameterService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeParameterServiceImpl implements FeeParameterService {

    private static final String ID_NOT_FOUND = "Investment Management not found with id: ";
    private static final String CODE_NOT_FOUND = "Investment Management not found with code: ";
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
        FeeParameterDTO feeParameterDTO = feeParameterMapper.mapFromCreateRequestToDto(createFeeParameterRequest);
        dataChangeDTO.setInputId(createFeeParameterRequest.getInputId());
        dataChangeDTO.setInputIPAddress(createFeeParameterRequest.getInputIPAddress());
        return processFeeParameterCreation(feeParameterDTO, dataChangeDTO);
    }

    @Override
    public FeeParameterResponse createMultipleData(FeeParameterListRequest createFeeParameterListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create multiple fee parameter with request: {}", createFeeParameterListRequest);
        dataChangeDTO.setInputId(createFeeParameterListRequest.getInputId());
        dataChangeDTO.setInputIPAddress(createFeeParameterListRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (FeeParameterDTO feeParameterDTO : createFeeParameterListRequest.getFeeParameterDTOList()) {
            FeeParameterResponse response = processFeeParameterCreation(feeParameterDTO, dataChangeDTO);
            totalDataSuccess += response.getTotalDataSuccess();
            totalDataFailed += response.getTotalDataFailed();
            errorMessageList.addAll(response.getErrorMessageDTOList());
        }

        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private FeeParameterResponse processFeeParameterCreation(FeeParameterDTO feeParameterDTO, BillingDataChangeDTO dataChangeDTO) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        try {
            List<String> validationErrors = new ArrayList<>();
            validationCodeAlreadyExists(feeParameterDTO.getCode(), validationErrors);
            validationNameAlreadyExists(feeParameterDTO.getName(), validationErrors);

            dataChangeDTO.setInputId(dataChangeDTO.getInputId());
            dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
            dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterDTO)));

            if (validationErrors.isEmpty()) {
                dataChangeService.createChangeActionADD(dataChangeDTO, FeeParameter.class);
                totalDataSuccess++;
            } else {
                totalDataFailed++;
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(feeParameterDTO.getCode(), validationErrors);
                errorMessageList.add(errorMessageDTO);
            }
        } catch (Exception e) {
            handleGeneralError(feeParameterDTO, e, errorMessageList);
            totalDataFailed++;
        }

        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public FeeParameterResponse createSingleApprove(FeeParameterApproveRequest approveRequest) {
        log.info("Approve single fee parameter with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        FeeParameterDTO feeParameterDTO = approveRequest.getData();

        try {
            List<String> validationErrors = new ArrayList<>();
            validationCodeAlreadyExists(feeParameterDTO.getCode(), validationErrors);
            validationNameAlreadyExists(feeParameterDTO.getName(), validationErrors);

            Errors errors = validateFeeParameterUsingValidator(feeParameterDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(objectError -> validationErrors.add(objectError.getDefaultMessage()));
            }

            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(Long.valueOf(approveRequest.getDataChangeId()));
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                FeeParameter feeParameter = feeParameterMapper.createEntity(feeParameterDTO, dataChangeDTO);
                feeParameterRepository.save(feeParameter);

                dataChangeDTO.setDescription("Successfully approve data change and save data fee parameter");
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameter)));
                dataChangeDTO.setEntityId(feeParameter.getId().toString());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(feeParameterDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public FeeParameterResponse updateSingleData(UpdateFeeParameterRequest updateFeeParameterRequest, BillingDataChangeDTO dataChangeDTO) {
        dataChangeDTO.setInputId(updateFeeParameterRequest.getInputId());
        dataChangeDTO.setInputIPAddress(updateFeeParameterRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        FeeParameterDTO feeParameterDTO = feeParameterMapper.mapFromUpdateRequestToDto(updateFeeParameterRequest);
        try {
            FeeParameter feeParameter = feeParameterRepository.findById(feeParameterDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + feeParameterDTO.getId()));

            AtomicInteger successCounter = new AtomicInteger(totalDataSuccess);
            AtomicInteger failedCounter = new AtomicInteger(totalDataFailed);

            processUpdateFeeParameter(feeParameter, feeParameterDTO, dataChangeDTO, errorMessageList, successCounter, failedCounter);

            totalDataSuccess = successCounter.get();
            totalDataFailed = failedCounter.get();
        } catch (Exception e) {
            handleGeneralError(feeParameterDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public FeeParameterResponse updateMultipleData(FeeParameterListRequest feeParameterListRequest, BillingDataChangeDTO dataChangeDTO) {
        dataChangeDTO.setInputId(feeParameterListRequest.getInputId());
        dataChangeDTO.setInputIPAddress(feeParameterListRequest.getInputIPAddress());
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        for (FeeParameterDTO feeParameterDTO : feeParameterListRequest.getFeeParameterDTOList()) {
            try {
                FeeParameter feeParameter = feeParameterRepository.findByCode(feeParameterDTO.getCode())
                        .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + feeParameterDTO.getCode()));

                AtomicInteger successCounter = new AtomicInteger(totalDataSuccess);
                AtomicInteger failedCounter = new AtomicInteger(totalDataFailed);

                processUpdateFeeParameter(feeParameter, feeParameterDTO, dataChangeDTO, errorMessageList, successCounter, failedCounter);

                totalDataSuccess = successCounter.get();
                totalDataFailed = failedCounter.get();
            } catch (Exception e) {
                handleGeneralError(feeParameterDTO, e, errorMessageList);
                totalDataFailed++;
            }
        }
        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    private void processUpdateFeeParameter(FeeParameter feeParameter,
                                                   FeeParameterDTO feeParameterDTO,
                                                   BillingDataChangeDTO dataChangeDTO,
                                                   List<ErrorMessageDTO> errorMessageList,
                                                   AtomicInteger successCounter,
                                                   AtomicInteger failedCounter) {
        try {
            dataChangeDTO.setInputId(dataChangeDTO.getInputId());
            dataChangeDTO.setInputIPAddress(dataChangeDTO.getInputIPAddress());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameter)));
            dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterDTO)));
            dataChangeDTO.setEntityId(feeParameter.getId().toString());

            dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
            successCounter.incrementAndGet(); // Increment totalDataSuccess
        } catch (Exception e) {
            handleGeneralError(feeParameterDTO, e, errorMessageList);
            failedCounter.incrementAndGet(); // Increment totalDataFailed
        }
    }


    @Override
    public FeeParameterResponse updateSingleApprove(FeeParameterApproveRequest approveRequest) {
        log.info("Approve when update fee parameter with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageList = new ArrayList<>();

        validateDataChangeId(approveRequest.getDataChangeId());
        FeeParameterDTO feeParameterDTO = approveRequest.getData();
        try {
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            List<String> validationErrors = new ArrayList<>();

            Errors errors = validateFeeParameterUsingValidator(feeParameterDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            FeeParameter feeParameter = feeParameterRepository.findByCode(feeParameterDTO.getCode())
                    .orElseThrow(() -> new DataNotFoundException(CODE_NOT_FOUND + feeParameterDTO.getCode()));

            // copy data DTO to Entity
            feeParameterMapper.mapObjects(feeParameterDTO, feeParameter);
            log.info("Fee Parameter after copy properties: {}", feeParameter);

            // Retrieve and set billing data change
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(approveRequest.getApproveIPAddress());
            dataChangeDTO.setEntityId(feeParameter.getId().toString());

            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                FeeParameter feeParameterUpdated = feeParameterMapper.updateEntity(feeParameter, dataChangeDTO);
                FeeParameter feeParameterSaved = feeParameterRepository.save(feeParameterUpdated);

                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(feeParameterSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update data entity");
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(feeParameterDTO, e, errorMessageList);
            totalDataFailed++;
        }
        return new FeeParameterResponse(totalDataSuccess, totalDataFailed, errorMessageList);
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
        return feeParameter.getValue();
    }

    @Override
    public List<FeeParameterDTO> getByNameList(List<String> nameList) {
        List<FeeParameter> feeParameterList = feeParameterRepository.findFeeParameterByNameList(nameList);
        // Check if all names are present in the feeParameterList
        for (String name : nameList) {
            Optional<FeeParameter> foundParameter = feeParameterList.stream()
                    .filter(parameter -> parameter.getName().equals(name))
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
                .collect(Collectors.toMap(FeeParameter::getName, FeeParameter::getValue));

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

    private void handleGeneralError(FeeParameterDTO feeParameterDTO, Exception e, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add("An unexpected error occurred: " + e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(feeParameterDTO != null ? feeParameterDTO.getCode() : UNKNOWN, validationErrors));
    }

    private void validateDataChangeId(String dataChangeId) {
        if (!dataChangeService.existById(Long.valueOf(dataChangeId))) {
            log.info("Data Change ids not found");
            throw new DataNotFoundException("Data Change ids not found");
        }
    }

}
