package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListRequest;
import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListResponse;
import com.bayu.billingservice.dto.investmentmanagement.ErrorMessageInvestmentManagementDTO;
import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import com.bayu.billingservice.exception.CreateDataException;
import com.bayu.billingservice.model.BillingDataChange;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.model.enumerator.ActionStatus;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.repository.BillingDataChangeRepository;
import com.bayu.billingservice.repository.InvestmentManagementRepository;
import com.bayu.billingservice.service.InvestmentManagementService;
import com.bayu.billingservice.util.EmailValidator;
import com.bayu.billingservice.util.TableNameResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentManagementServiceImpl implements InvestmentManagementService {

    private final InvestmentManagementRepository investmentManagementRepository;
    private final BillingDataChangeRepository dataChangeRepository;
    private final EmailValidator emailValidator;
    private final ObjectMapper objectMapper;

    @Override
    public Boolean checkExistByCode(String code) {
        // TRUE means the data is in the table
        Boolean existedByCode = investmentManagementRepository.existsByCode(code);
        log.info("Existed by code: {}", existedByCode);
        return existedByCode;
    }

    @Override
    public CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Request data: {}", investmentManagementListRequest);
        String inputId = investmentManagementListRequest.getInputId();
        String inputIPAddress = investmentManagementListRequest.getInputIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed= 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageInvestmentManagementDTOList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO investmentManagementDTO : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                // TODO: 1. Validation data request
                List<String> errorValidationList = checkValidationDataRequest(investmentManagementDTO);
                List<String> errorMessages = new ArrayList<>(errorValidationList);

                String code = investmentManagementDTO.getCode();

                // TODO: 2. Check code for make sure is not exist in table, because code is unique
                Boolean existsByCode = investmentManagementRepository.existsByCode(code);
                if (Boolean.TRUE.equals(existsByCode)) {
                    errorMessages.add("Code '" + code + "' is already taken");
                }

                // TODO: 3. Checking list error messages
                if (errorMessages.isEmpty()) {
                    // TODO: Create entity Data Change
                    String jsonDataAfter = objectMapper.writeValueAsString(investmentManagementDTO);

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
                    totalDataSuccess++;
                } else {
                    ErrorMessageInvestmentManagementDTO errorMessageInvestmentManagementDTO = ErrorMessageInvestmentManagementDTO.builder()
                            .code(code)
                            .errorMessageList(errorMessages)
                            .build();
                    errorMessageInvestmentManagementDTOList.add(errorMessageInvestmentManagementDTO);
                    totalDataFailed++;
                }
            }

            return CreateInvestmentManagementListResponse.builder()
                    .totalDataSuccess(totalDataSuccess)
                    .totalDataFailed(totalDataFailed)
                    .errorMessages(errorMessageInvestmentManagementDTOList)
                    .build();
        } catch (Exception e) {
            log.error("Error when create list billing customer: {}", e.getMessage());
            throw new CreateDataException("Error when create list billing customer: " + e.getMessage(), e);
        }
    }

    @Override
    public CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        return null;
    }

    public List<String> checkValidationDataRequest(InvestmentManagementDTO investmentManagementDTO) {
        List<String> errorMessages = new ArrayList<>();

        if (investmentManagementDTO.getCode().isEmpty()) {
            errorMessages.add("Code cannot be empty");
        }

        if (investmentManagementDTO.getName().isEmpty()) {
            errorMessages.add("Name cannot be empty");
        }

        String email = investmentManagementDTO.getEmail();
        if (email.isEmpty()) {
            errorMessages.add("Email cannot be empty");
        } else {
            // Check if email is valid using emailValidator
            if (!emailValidator.isValidEmail(email)) {
                errorMessages.add("Email is not valid");
            }
        }


        return errorMessages;
    }
}
