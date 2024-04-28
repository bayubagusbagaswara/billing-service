package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.exception.CreateDataException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.BillingDataChange;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.model.enumerator.ActionStatus;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.repository.BillingDataChangeRepository;
import com.bayu.billingservice.repository.InvestmentManagementRepository;
import com.bayu.billingservice.service.InvestmentManagementService;
import com.bayu.billingservice.util.StringUtil;
import com.bayu.billingservice.util.TableNameResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentManagementServiceImpl implements InvestmentManagementService {

    private final InvestmentManagementRepository investmentManagementRepository;
    private final BillingDataChangeRepository dataChangeRepository;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return investmentManagementRepository.existsByCode(code);
    }

    @Override
    public CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Request data: {}", investmentManagementListRequest);
        String inputId = investmentManagementListRequest.getInputId();
        String inputIPAddress = investmentManagementListRequest.getInputIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed= 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        for (InvestmentManagementDTO dto : investmentManagementListRequest.getInvestmentManagementRequestList()) {
            List<String> errorMessages = new ArrayList<>();
            Errors errors = validateInvestmentManagementDTO(dto);

            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
            }

            if (isCodeAlreadyExists(dto.getCode())) {
                errorMessages.add("Code '" + dto.getCode() + "' is already taken");
            }

            if (errorMessages.isEmpty()) {
                try {
                    saveToDataChange(dto, inputId, inputIPAddress);
                    totalDataSuccess++;
                } catch (Exception e) {
                    log.error("Error saving investment management data for DTO: {}", dto, e);
                    errorMessages.add("Failed to save data: " + e.getMessage());
                    totalDataFailed++;
                }
            } else {
                ErrorMessageInvestmentManagementDTO errorMessageDTO = new ErrorMessageInvestmentManagementDTO();
                errorMessageDTO.setCode(dto.getCode());
                errorMessageDTO.setErrorMessages(errorMessages);
                errorMessageList.add(errorMessageDTO);
                totalDataFailed++;
            }
        }

        return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        return null;
    }

    @Override
    public UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Request data: {}", investmentManagementListRequest);
        Long dataChangeId = investmentManagementListRequest.getDataChangeId();
        String inputId = investmentManagementListRequest.getInputId();
        String inputIPAddress = investmentManagementListRequest.getInputIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO dto : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateInvestmentManagementDTO(dto);

                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
                }

                if (isCodeAlreadyExists(dto.getCode())) {
                    errorMessages.add("Code '" + dto.getCode() + "' is already taken");
                }

                if (errorMessages.isEmpty()) {
                    InvestmentManagement investmentManagementEntity = investmentManagementRepository.findById(dto.getId())
                            .orElseThrow(() -> new DataNotFoundException("Investment Management with id '" + dto.getId() + "' not found"));

                    String jsonDataBefore = objectMapper.writeValueAsString(investmentManagementEntity);
                    String jsonDataAfter = objectMapper.writeValueAsString(dto);
                    BillingDataChange dataChangeEntity = dataChangeRepository.findById(dataChangeId)
                            .orElseThrow(() -> new DataNotFoundException("Data Change with id '" + dataChangeId + "' not found"));
                    dataChangeEntity.setActionStatus(ActionStatus.EDIT);
                    dataChangeEntity.setInputId(inputId);
                    dataChangeEntity.setInputIPAddress(inputIPAddress);
                    dataChangeEntity.setInputDate(new Date());
                    dataChangeEntity.setJsonDataBefore(jsonDataBefore);
                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);

                    dataChangeRepository.save(dataChangeEntity);
                    totalDataSuccess++;
                } else {
                    ErrorMessageInvestmentManagementDTO errorMessageDTO = new ErrorMessageInvestmentManagementDTO();
                    errorMessageDTO.setCode(dto.getCode());
                    errorMessageDTO.setErrorMessages(errorMessages);
                    errorMessageList.add(errorMessageDTO);
                    totalDataFailed++;
                }
            }

            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (Exception e) {
            log.error("Error when update list: {}", e.getMessage());
            throw new CreateDataException("Error update investment management data", e);
        }
    }

    private void saveToDataChange(InvestmentManagementDTO dto, String inputId, String inputIPAddress) {
      try {
            String jsonDataAfter = objectMapper.writeValueAsString(dto);
            BillingDataChange billingDataChange = BillingDataChange.builder()
                    .approvalStatus(ApprovalStatus.PENDING)
                    .inputId(inputId)
                    .inputDate(new Date())
                    .inputIPAddress(inputIPAddress)
                    .approveId("")
                    .approveDate(null)
                    .approveIPAddress("")
                    .actionStatus(ActionStatus.ADD)
                    .entityClassName(InvestmentManagement.class.getName())
                    .tableName(TableNameResolver.getTableName(InvestmentManagement.class))
                    .jsonDataBefore("")
                    .jsonDataAfter(jsonDataAfter)
                    .description("")
                    .build();

            dataChangeRepository.save(billingDataChange);
        } catch (JsonProcessingException e) {
            log.error("Error serializing InvestmentManagementDTO to JSON: {}", e.getMessage());
            throw new CreateDataException("Error serializing InvestmentManagementDTO to JSON", e);
        } catch (Exception e) {
            log.error("Error saving investment management data: {}", e.getMessage());
            throw new CreateDataException("Error saving investment management data", e);
        }
    }

    private void saveToEntity(InvestmentManagementDTO dto, String approve) {
        // update Data Change and save to Entity
    }

    public Errors validateInvestmentManagementDTO(InvestmentManagementDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "investmentManagementDTO");
        validator.validate(dto, errors);
        return errors;
    }

}
