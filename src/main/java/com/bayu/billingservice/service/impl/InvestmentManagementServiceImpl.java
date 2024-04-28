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

            validationCodeAlreadyExists(dto, errorMessages);

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

    private void validationCodeAlreadyExists(InvestmentManagementDTO dto, List<String> errorMessages) {
        if (isCodeAlreadyExists(dto.getCode())) {
            errorMessages.add("Code '" + dto.getCode() + "' is already taken");
        }
    }

    @Override
    public CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Request data create list approve: {}", investmentManagementListRequest);
        Long dataChangeId = investmentManagementListRequest.getDataChangeId();
        String approveId = investmentManagementListRequest.getApproveId();
        String approveIPAddress = investmentManagementListRequest.getApproveIPAddress();
        Date approveDate = new Date();
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

                validationCodeAlreadyExists(dto, errorMessages);

                BillingDataChange dataChange = getBillingDataChangeById(dataChangeId);

                if (errorMessages.isEmpty()) {
                    // do create and save to table
                    InvestmentManagement investmentManagement = InvestmentManagement.builder()
                            .code(dto.getCode())
                            .name(dto.getName())
                            .email(dto.getEmail())
                            .address1(dto.getAddress1())
                            .address2(dto.getAddress2())
                            .address3(dto.getAddress3())
                            .address4(dto.getAddress4())
                            .build();
                    investmentManagementRepository.save(investmentManagement);

                    String jsonDataAfter = objectMapper.writeValueAsString(investmentManagement);
                    dataChange.setApprovalStatus(ApprovalStatus.APPROVED);
                    dataChange.setApproveId(approveId);
                    dataChange.setApproveIPAddress(approveIPAddress);
                    dataChange.setApproveDate(approveDate);
                    dataChange.setJsonDataBefore("");
                    dataChange.setJsonDataAfter(jsonDataAfter);
                    dataChange.setDescription("Success approve and save entity");

                    dataChangeRepository.save(dataChange);
                    totalDataSuccess++;
                } else {
                    String jsonDataAfter = objectMapper.writeValueAsString(dto);
                    dataChange.setApprovalStatus(ApprovalStatus.REJECTED);
                    dataChange.setApproveId(approveId);
                    dataChange.setApproveIPAddress(approveIPAddress);
                    dataChange.setApproveDate(approveDate);
                    dataChange.setJsonDataBefore("");
                    dataChange.setJsonDataAfter(jsonDataAfter);
                    dataChange.setDescription(StringUtil.joinStrings(errorMessages));

                    dataChangeRepository.save(dataChange);
                    totalDataFailed++;
                }
            }

            return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (Exception e) {
            log.error("Error when create list approve: {}", e.getMessage());
            throw new CreateDataException("Error create saving investment management data", e);
        }
    }

    private BillingDataChange getBillingDataChangeById(Long dataChangeId) {
        return dataChangeRepository.findById(dataChangeId)
                .orElseThrow(() -> new DataNotFoundException("Data Change not found with id: " + dataChangeId));
    }

    @Override
    public UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Request data update list: {}", investmentManagementListRequest);
        Long dataChangeId = investmentManagementListRequest.getDataChangeId();
        String inputId = investmentManagementListRequest.getInputId();
        Date inputDate = new Date();
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

                validationCodeAlreadyExists(dto, errorMessages);

                if (errorMessages.isEmpty()) {
                    InvestmentManagement investmentManagementEntity = investmentManagementRepository.findById(dto.getId())
                            .orElseThrow(() -> new DataNotFoundException("Investment Management not found with id:" + dto.getId()));

                    String jsonDataBefore = objectMapper.writeValueAsString(investmentManagementEntity);
                    String jsonDataAfter = objectMapper.writeValueAsString(dto);
                    BillingDataChange dataChangeEntity = getBillingDataChangeById(dataChangeId);
                    dataChangeEntity.setActionStatus(ActionStatus.EDIT);
                    dataChangeEntity.setInputId(inputId);
                    dataChangeEntity.setInputIPAddress(inputIPAddress);
                    dataChangeEntity.setInputDate(inputDate);
                    dataChangeEntity.setJsonDataBefore(jsonDataBefore);
                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);
                    dataChangeEntity.setDescription("Success save data change");

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

    @Override
    public UpdateInvestmentManagementListResponse updateListApprove(UpdateInvestmentManagementListRequest investmentManagementListRequest) {
        // data yg penting adalah approve
        log.info("Request data update approve: {}", investmentManagementListRequest);
        Long dataChangeId = investmentManagementListRequest.getDataChangeId();
        String approveId = investmentManagementListRequest.getApproveId();
        String approveIPAddress = investmentManagementListRequest.getApproveIPAddress();
        Date approveDate = new Date();
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

                validationCodeAlreadyExists(dto, errorMessages);

                InvestmentManagement investmentManagementEntity = investmentManagementRepository.findById(dto.getId())
                        .orElseThrow(() -> new DataNotFoundException("Investment Management not found with id: " + dto.getId()));

                BillingDataChange dataChangeEntity = getBillingDataChangeById(dataChangeId);

                if (errorMessages.isEmpty()) {
                    // do update entity
                    investmentManagementEntity.setCode(dto.getCode());
                    investmentManagementEntity.setName(dto.getName());
                    investmentManagementEntity.setEmail(dto.getEmail());
                    investmentManagementEntity.setAddress1(dto.getAddress1());
                    investmentManagementEntity.setAddress2(dto.getAddress2());
                    investmentManagementEntity.setAddress3(dto.getAddress3());
                    investmentManagementEntity.setAddress4(dto.getAddress4());
                    InvestmentManagement investmentManagementSaved = investmentManagementRepository.save(investmentManagementEntity);

                    String jsonDataBefore = objectMapper.writeValueAsString(investmentManagementEntity);
                    String jsonDataAfter = objectMapper.writeValueAsString(investmentManagementSaved);

                    dataChangeEntity.setApprovalStatus(ApprovalStatus.APPROVED);
                    dataChangeEntity.setApproveId(approveId);
                    dataChangeEntity.setApproveIPAddress(approveIPAddress);
                    dataChangeEntity.setApproveDate(approveDate);
                    dataChangeEntity.setJsonDataBefore(jsonDataBefore);
                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);
                    dataChangeEntity.setDescription("Success Approve");

                    dataChangeRepository.save(dataChangeEntity);
                    totalDataSuccess++;
                } else {
                    // do update data change, with approval status is REJECT and put error messages to description
                    String jsonDataBefore = objectMapper.writeValueAsString(investmentManagementEntity);
                    String jsonDataAfter = objectMapper.writeValueAsString(dto);

                    dataChangeEntity.setApprovalStatus(ApprovalStatus.REJECTED);
                    dataChangeEntity.setApproveId(approveId);
                    dataChangeEntity.setApproveIPAddress(approveIPAddress);
                    dataChangeEntity.setApproveDate(approveDate);
                    dataChangeEntity.setJsonDataBefore(jsonDataBefore);
                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);
                    dataChangeEntity.setDescription(StringUtil.joinStrings(errorMessages));

                    dataChangeRepository.save(dataChangeEntity);
                    totalDataFailed++;
                }
            }

            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (Exception e) {
            log.error("Error when update list approve: {}", e.getMessage());
            throw new CreateDataException("Error saving investment management data", e);
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

    public Errors validateInvestmentManagementDTO(InvestmentManagementDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "investmentManagementDTO");
        validator.validate(dto, errors);
        return errors;
    }

}
