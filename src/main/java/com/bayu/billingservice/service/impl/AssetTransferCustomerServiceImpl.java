package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.assettransfercustomer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.mapper.AssetTransferCustomerMapper;
import com.bayu.billingservice.model.AssetTransferCustomer;
import com.bayu.billingservice.repository.AssetTransferCustomerRepository;
import com.bayu.billingservice.service.AssetTransferCustomerService;
import com.bayu.billingservice.service.DataChangeService;
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
public class AssetTransferCustomerServiceImpl implements AssetTransferCustomerService {

    private static final String ID_NOT_FOUND = "Asset Transfer Customer not found with id: ";
    private static final String CODE_NOT_FOUND = "Asset Transfer Customer not found with code: ";
    private static final String UNKNOWN = "unknown";

    private final AssetTransferCustomerRepository assetTransferCustomerRepository;
    private final DataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final AssetTransferCustomerMapper assetTransferCustomerMapper;

    @Override
    public AssetTransferCustomerResponse createSingleData(CreateAssetTransferCustomerRequest createAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single data asset transfer customer with request: {}", createAssetTransferCustomerRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        AssetTransferCustomerDTO assetTransferCustomerDTO = null;

        try {
            /* maps request data to dto */
            assetTransferCustomerDTO = assetTransferCustomerMapper.mapCreateRequestToDto(createAssetTransferCustomerRequest);

            /* validation for each dto field */
            Errors errors = validateAssetTransferCustomerUsingValidator(assetTransferCustomerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* sets input id to a DataChange object */
            dataChangeDTO.setInputId(createAssetTransferCustomerRequest.getInputId());

            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = getErrorMessageDTO(assetTransferCustomerDTO, validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(assetTransferCustomerDTO)));
                dataChangeService.createChangeActionADD(dataChangeDTO, AssetTransferCustomer.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(assetTransferCustomerDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new AssetTransferCustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public AssetTransferCustomerResponse createSingleApprove(AssetTransferCustomerApproveRequest approveRequest, String clientIP) {
        log.info("Approve when create asset transfer customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        AssetTransferCustomerDTO assetTransferCustomerDTO = null;

        try {
            /* validate dataChangeId whether it exists or not */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* get data from DataChange, then map the JsonDataAfter data to the AssetTransferCustomer dto class */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);


        } catch (Exception e) {
            handleGeneralError(assetTransferCustomerDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new AssetTransferCustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public AssetTransferCustomerResponse updateSingleData(UpdateAssetTransferCustomerRequest updateAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public AssetTransferCustomerResponse updateSingleApprove(AssetTransferCustomerApproveRequest updateAssetTransferCustomerListRequest, String clientIP) {
        return null;
    }

    @Override
    public AssetTransferCustomerResponse deleteSingleData(DeleteAssetTransferCustomerRequest deleteAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        return null;
    }

    @Override
    public AssetTransferCustomerResponse deleteSingleApprove(AssetTransferCustomerApproveRequest deleteAssetTransferCustomerListRequest, String clientIP) {
        return null;
    }

    @Override
    public String deleteAll() {
        return "";
    }

    @Override
    public List<AssetTransferCustomerDTO> getAll() {
        return List.of();
    }

    private static ErrorMessageDTO getErrorMessageDTO(AssetTransferCustomerDTO assetTransferCustomerDTO, List<String> validationErrors) {
        String string = assetTransferCustomerDTO.getId() == null ? UNKNOWN : assetTransferCustomerDTO.getId().toString();
        return new ErrorMessageDTO(string, validationErrors);
    }

    public Errors validateAssetTransferCustomerUsingValidator(AssetTransferCustomerDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "assetTransferCustomerDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void validateDataChangeId(String dataChangeId) {
        if (dataChangeService.existById(Long.valueOf(dataChangeId))) {
            log.info("Data Change id not found");
            throw new DataNotFoundException("Data Change not found with id: " + dataChangeId);
        }
    }

    private void handleGeneralError(AssetTransferCustomerDTO assetTransferCustomerDTO, Exception e, List<String> validationErrors, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(assetTransferCustomerDTO != null ? String.valueOf(assetTransferCustomerDTO.getId()) : UNKNOWN, validationErrors));
    }

    public void copyNonNullOrEmptyFields(AssetTransferCustomer assetTransferCustomer, AssetTransferCustomerDTO assetTransferCustomerDTO) {
        try {
            Map<String, String> entityProperties = BeanUtils.describe(assetTransferCustomer);

            for (Map.Entry<String, String> entry : entityProperties.entrySet()) {
                String propertyName = entry.getKey();
                String entityValue = entry.getValue();

                String dtoValue = BeanUtils.getProperty(assetTransferCustomerDTO, propertyName);

                if (isNullOrEmpty(dtoValue) && entityValue != null) {
                    BeanUtils.setProperty(assetTransferCustomerDTO, propertyName, entityValue);
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
