package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.assettransfercustomer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.GeneralException;
import com.bayu.billingservice.mapper.AssetTransferCustomerMapper;
import com.bayu.billingservice.model.AssetTransferCustomer;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.AssetTransferCustomerRepository;
import com.bayu.billingservice.service.AssetTransferCustomerService;
import com.bayu.billingservice.service.DataChangeService;
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
public class AssetTransferCustomerServiceImpl implements AssetTransferCustomerService {

    private static final String ID_NOT_FOUND = "Asset Transfer Customer not found with id: ";
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
            assetTransferCustomerDTO = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), AssetTransferCustomerDTO.class);

            /* perform data validation if necessary, for example checking whether the code already exists in the database */
            Errors errors = validateAssetTransferCustomerUsingValidator(assetTransferCustomerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* sets approveId and approveIPAddress to a DataChange object */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);

            /* check the number of contents of the ValidationErrors object, then map it to the response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(assetTransferCustomerDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
            } else {
                AssetTransferCustomer assetTransferCustomer = assetTransferCustomerMapper.createEntity(assetTransferCustomerDTO, dataChangeDTO);
                assetTransferCustomerRepository.save(assetTransferCustomer);
                dataChangeDTO.setDescription("Successfully approve data change and save data asset transfer customer with id: " + assetTransferCustomer.getId());
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(assetTransferCustomer)));
                dataChangeDTO.setEntityId(assetTransferCustomer.getId().toString());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(assetTransferCustomerDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new AssetTransferCustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public AssetTransferCustomerResponse updateSingleData(UpdateAssetTransferCustomerRequest updateAssetTransferCustomerRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update single data asset transfer customer with request: {}", updateAssetTransferCustomerRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        AssetTransferCustomerDTO clonedDTO = null;

        try {
            /* maps request data to dto */
            AssetTransferCustomerDTO assetTransferCustomerDTO = assetTransferCustomerMapper.mapUpdateRequestToDto(updateAssetTransferCustomerRequest);
            log.info("[Update Single] Asset transfer customer dto: {}", assetTransferCustomerDTO);

            /* clone dto */
            clonedDTO = new AssetTransferCustomerDTO();
            BeanUtil.copyAllProperties(assetTransferCustomerDTO, clonedDTO);
            log.info("[Update Single] Result mapping request to dto: {}", assetTransferCustomerDTO);

            /* get asset transfer customer by id */
            AssetTransferCustomer assetTransferCustomer = assetTransferCustomerRepository.findById(assetTransferCustomerDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + assetTransferCustomerDTO.getId()));

            /* validation for each dto field */
            Errors errors = validateAssetTransferCustomerUsingValidator(clonedDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* sets inputId to a DataChange object */
            dataChangeDTO.setInputId(updateAssetTransferCustomerRequest.getInputId());

            /* check the number of content of the ValidationErrors object, then map it to the response */
            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = getErrorMessageDTO(assetTransferCustomerDTO, validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(assetTransferCustomer)));
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(assetTransferCustomerDTO)));
                dataChangeDTO.setEntityId(assetTransferCustomer.getId().toString());
                dataChangeService.createChangeActionEDIT(dataChangeDTO, AssetTransferCustomer.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(clonedDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new AssetTransferCustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public AssetTransferCustomerResponse updateSingleApprove(AssetTransferCustomerApproveRequest approveRequest, String clientIP) {
        log.info("Approve when update asset transfer customer with request: {}", approveRequest);
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
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());
            AssetTransferCustomerDTO dto = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), AssetTransferCustomerDTO.class);
            log.info("[Update Approve] Map data from JsonDataAfter to dto: {}", dto);

            /* get asset transfer customer by id */
            AssetTransferCustomer assetTransferCustomer = assetTransferCustomerRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            /* map data from dto to entity, to overwrite new data */
            assetTransferCustomerMapper.mapObjectsDtoToEntity(dto, assetTransferCustomer);
            log.info("[Update Approve] Map object dto to entity: {}", assetTransferCustomer);

            /* map from entity to dto */
            assetTransferCustomerDTO = assetTransferCustomerMapper.mapToDto(assetTransferCustomer);
            log.info("[Update Approve] Map from entity to dto: {}", assetTransferCustomerDTO);

            /* check validation each column dto */
            Errors errors = validateAssetTransferCustomerUsingValidator(assetTransferCustomerDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* sets approveId, approveIPAddress, and entityId to a DataChange object */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setEntityId(assetTransferCustomer.getId().toString());

            /* check the number of contents of the ValidationErrors object, then map it to the response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(assetTransferCustomerDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                AssetTransferCustomer assetTransferCustomerUpdated = assetTransferCustomerMapper.updateEntity(assetTransferCustomer, dataChangeDTO);
                AssetTransferCustomer assetTransferCustomerSaved = assetTransferCustomerRepository.save(assetTransferCustomerUpdated);
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(assetTransferCustomerSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update asset transfer customer entity with id: " + assetTransferCustomerSaved.getId());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(assetTransferCustomerDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new AssetTransferCustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public AssetTransferCustomerResponse deleteSingleData(DeleteAssetTransferCustomerRequest deleteRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Delete single asset transfer customer with request: {}", deleteRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        AssetTransferCustomerDTO assetTransferCustomerDTO = null;

        try {
            /* get asset transfer customer by id */
            Long id = deleteRequest.getId();
            AssetTransferCustomer assetTransferCustomer = assetTransferCustomerRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + id));

            /* maps entity to dto */
            assetTransferCustomerDTO = assetTransferCustomerMapper.mapToDto(assetTransferCustomer);

            /* set data change */
            dataChangeDTO.setInputId(deleteRequest.getInputId());
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(assetTransferCustomer)));
            dataChangeDTO.setJsonDataAfter("");
            dataChangeDTO.setEntityId(assetTransferCustomer.getId().toString());
            dataChangeService.createChangeActionDELETE(dataChangeDTO, InvestmentManagement.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(assetTransferCustomerDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new AssetTransferCustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public AssetTransferCustomerResponse deleteSingleApprove(AssetTransferCustomerApproveRequest approveRequest, String clientIP) {
        log.info("Approve when delete asset transfer customer with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        AssetTransferCustomerDTO assetTransferCustomerDTO = null;

        try {
            /*  validate dataChangeId whether it exists or not */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* get data from DataChange */
            Long dataChangeId = Long.valueOf(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());

            /* get investment management by id */
            AssetTransferCustomer assetTransferCustomer = assetTransferCustomerRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            /* maps from entity to dto */
            assetTransferCustomerDTO = assetTransferCustomerMapper.mapToDto(assetTransferCustomer);

            /* set data change for approve id and approve ip address */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(assetTransferCustomerMapper.mapToDto(assetTransferCustomer))));
            dataChangeDTO.setDescription("Successfully approve data change and delete asset transfer customer with id: " + assetTransferCustomer.getId());
            dataChangeService.approvalStatusIsApproved(dataChangeDTO);

            /* delete data entity in the database */
            assetTransferCustomerRepository.delete(assetTransferCustomer);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(assetTransferCustomerDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new AssetTransferCustomerResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public String deleteAll() {
        assetTransferCustomerRepository.deleteAll();
        return "Successfully delete all asset transfer customer data";
    }

    @Override
    public List<AssetTransferCustomerDTO> getAll() {
        List<AssetTransferCustomer> all = assetTransferCustomerRepository.findAll();
        return assetTransferCustomerMapper.mapToDTOList(all);
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
