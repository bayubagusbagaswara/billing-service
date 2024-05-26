package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.exchangerate.*;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.mapper.ExchangeRateMapper;
import com.bayu.billingservice.model.ExchangeRate;
import com.bayu.billingservice.repository.ExchangeRateRepository;
import com.bayu.billingservice.service.DataChangeService;
import com.bayu.billingservice.service.ExchangeRateService;
import com.bayu.billingservice.util.BeanUtil;
import com.bayu.billingservice.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final String ID_NOT_FOUND = "Exchange Rate not found with id: ";
    private static final String UNKNOWN = "unknown";

    private final ExchangeRateRepository exchangeRateRepository;
    private final DataChangeService dataChangeService;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final ExchangeRateMapper exchangeRateMapper;

    @Override
    public boolean isCurrencyAlreadyExists(String currency) {
        return exchangeRateRepository.existsByCurrency(currency);
    }

    @Override
    public List<ExchangeRateDTO> getAll() {
        List<ExchangeRate> all = exchangeRateRepository.findAll();
        return exchangeRateMapper.mapToDTOList(all);
    }

    @Override
    public ExchangeRateDTO getByCurrency(String currency) {
        ExchangeRate exchangeRate = exchangeRateRepository.findByCurrency(currency)
                .orElseThrow(() -> new DataNotFoundException("Exchange Rate not found with currency: " + currency));
        return exchangeRateMapper.mapToDto(exchangeRate);
    }

    @Override
    public ExchangeRateResponse createSingleData(CreateExchangeRateRequest createExchangeRateRequest) {
        log.info("Create single exchange rate with request: {}", createExchangeRateRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        ExchangeRateDTO exchangeRateDTO = null;

        try {
            /* mapping data from request to dto */
            exchangeRateDTO = exchangeRateMapper.mapCreateRequestToDto(createExchangeRateRequest);
            log.info("Exchange Rate DTO: {}", exchangeRateDTO);

            /* validation for each column dto */
            Errors errors = validateExchangeRateUsingValidator(exchangeRateDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* validating currency already exists */
            validationCurrencyAlreadyExists(exchangeRateDTO.getCurrency(), validationErrors);

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(exchangeRateDTO.getCurrency(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                ExchangeRate exchangeRate = exchangeRateMapper.createEntity(exchangeRateDTO, new BillingDataChangeDTO());
                log.info("Entity: {}", exchangeRate);
                exchangeRateRepository.save(exchangeRate);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(exchangeRateDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new ExchangeRateResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public ExchangeRateResponse updateSingleData(UpdateExchangeRateRequest updateExchangeRateRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update single data exchange rate with request: {}", updateExchangeRateRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        ExchangeRateDTO clonedDTO = null;

        try {
            /* mapping data from request to dto */
            ExchangeRateDTO exchangeRateDTO = exchangeRateMapper.mapUpdateRequestToDto(updateExchangeRateRequest);
            clonedDTO = new ExchangeRateDTO();
            BeanUtil.copyAllProperties(exchangeRateDTO, clonedDTO);
            log.info("[Update Single] Result mapping request to dto: {}", exchangeRateDTO);

            /* get exchange rate by id */
            ExchangeRate exchangeRate = exchangeRateRepository.findById(exchangeRateDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + exchangeRateDTO.getId()));

            /* data yang akan di validator*/
            if (clonedDTO.getDate() == null) {
                clonedDTO.setDate(exchangeRate.getDate());
            }

            if (clonedDTO.getCurrency() == null || clonedDTO.getCurrency().isEmpty()) {
                clonedDTO.setCurrency(exchangeRate.getCurrency());
            }

            if (clonedDTO.getValue() == null || clonedDTO.getValue().isEmpty()) {
                clonedDTO.setValue(exchangeRate.getValue().toPlainString());
            }

            log.info("[Update Single] Result map object entity to dto: {}", clonedDTO);

            /* check validator for data request after mapping to dto */
            Errors errors = validateExchangeRateUsingValidator(clonedDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set input id for data change */
            dataChangeDTO.setInputId(updateExchangeRateRequest.getInputId());

            /* check validation errors for customer response */
            if (!validationErrors.isEmpty()) {
                ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(exchangeRateDTO.getId().toString(), validationErrors);
                errorMessageDTOList.add(errorMessageDTO);
                totalDataFailed++;
            } else {
                dataChangeDTO.setJsonDataBefore(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(exchangeRate)));
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonDataUpdate(objectMapper.writeValueAsString(exchangeRateDTO)));
                dataChangeDTO.setEntityId(exchangeRate.getId().toString());
                dataChangeService.createChangeActionEDIT(dataChangeDTO, ExchangeRate.class);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(clonedDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new ExchangeRateResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public ExchangeRateResponse updateApprove(ExchangeRateApproveRequest approveRequest, String clientIP) {
        log.info("Approve when update exchange rate with request: {}", approveRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        ExchangeRateDTO exchangeRateDTO = null;

        try {
            /* validating data change id */
            validateDataChangeId(approveRequest.getDataChangeId());

            /* get data change by id and get json data after */
            Long dataChangeId = Long.parseLong(approveRequest.getDataChangeId());
            BillingDataChangeDTO dataChangeDTO = dataChangeService.getById(dataChangeId);
            Long entityId = Long.valueOf(dataChangeDTO.getEntityId());
            ExchangeRateDTO dto = objectMapper.readValue(dataChangeDTO.getJsonDataAfter(), ExchangeRateDTO.class);
            log.info("[Update Approve] Map data from JSON data after data change: {}", dto);

            /* get exchange rate by id */
            ExchangeRate exchangeRate = exchangeRateRepository.findById(entityId)
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + entityId));

            exchangeRateMapper.mapObjectsDtoToEntity(dto, exchangeRate);
            log.info("[Update Approve] Map object dto to entity: {}", exchangeRate);

            exchangeRateDTO = exchangeRateMapper.mapToDto(exchangeRate);
            log.info("[Update Approve] Map from entity to dto: {}", exchangeRateDTO);

            /* check validation each column dto */
            Errors errors = validateExchangeRateUsingValidator(exchangeRateDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));
            }

            /* set data change approve id and approve ip address */
            dataChangeDTO.setApproveId(approveRequest.getApproveId());
            dataChangeDTO.setApproveIPAddress(clientIP);
            dataChangeDTO.setEntityId(exchangeRate.getId().toString());

            /* check validation errors for custom response */
            if (!validationErrors.isEmpty()) {
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(exchangeRateDTO)));
                dataChangeService.approvalStatusIsRejected(dataChangeDTO, validationErrors);
                totalDataFailed++;
            } else {
                ExchangeRate exchangeRateUpdated = exchangeRateMapper.updateEntity(exchangeRate, dataChangeDTO);
                ExchangeRate exchangeRateSaved = exchangeRateRepository.save(exchangeRateUpdated);
                dataChangeDTO.setJsonDataAfter(JsonUtil.cleanedJsonData(objectMapper.writeValueAsString(exchangeRateSaved)));
                dataChangeDTO.setDescription("Successfully approve data change and update exchange rate entity with id: " + exchangeRateSaved.getId());
                dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(exchangeRateDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new ExchangeRateResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public String deleteAll() {
        exchangeRateRepository.deleteAll();
        return "Successfully delete all exchange rate from database";
    }

    public Errors validateExchangeRateUsingValidator(ExchangeRateDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "exchangeRateDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void validationCurrencyAlreadyExists(String currency, List<String> errorMessages) {
        if (isCurrencyAlreadyExists(currency)) {
            errorMessages.add("Exchange Rate is already taken with currency: " + currency);
        }
    }

    private void validateDataChangeId(String dataChangeId) {
        if (dataChangeService.existById(Long.valueOf(dataChangeId))) {
            log.info("Data Change id not found");
            throw new DataNotFoundException("Data Change not found with id: " + dataChangeId);
        }
    }

    private void handleGeneralError(ExchangeRateDTO exchangeRateDTO, Exception e, List<String> validationErrors, List<ErrorMessageDTO> errorMessageList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        validationErrors.add(e.getMessage());
        errorMessageList.add(new ErrorMessageDTO(exchangeRateDTO != null ? exchangeRateDTO.getCurrency() : UNKNOWN, validationErrors));
    }

}
