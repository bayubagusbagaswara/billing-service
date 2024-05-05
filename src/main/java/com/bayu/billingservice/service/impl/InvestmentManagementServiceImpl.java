package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.exception.DataChangeException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.DataProcessingException;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.InvestmentManagementRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.service.InvestmentManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
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

    private static final String ID_NOT_FOUND = "Investment Management not found with id: ";

    @Override
    public boolean isCodeAlreadyExists(String code) {
        return investmentManagementRepository.existsByCode(code);
    }

    @Override
    public CreateInvestmentManagementListResponse create(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single investment management with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            InvestmentManagementDTO investmentManagementDTO = InvestmentManagementDTO.builder()
                    .code(request.getCode())
                    .name(request.getName())
                    .email(request.getEmail())
                    .address1(request.getAddress1())
                    .address2(request.getAddress2())
                    .address3(request.getAddress3())
                    .address4(request.getAddress4())
                    .build();

            List<String> errorMessages = new ArrayList<>();
            Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));
            }

            validationCodeAlreadyExists(investmentManagementDTO.getCode(), errorMessages);

            dataChangeDTO.setInputId(request.getInputId());
            dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
            dataChangeDTO.setJsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO));

            if (errorMessages.isEmpty()) {
                dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
                totalDataSuccess++;
            } else {
                totalDataFailed++;
                ErrorMessageInvestmentManagementDTO errorMessageDTO = new ErrorMessageInvestmentManagementDTO(investmentManagementDTO.getCode(), errorMessages);
                errorMessageList.add(errorMessageDTO);
            }
            return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }
        return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create investment management list with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO investmentManagementDTO : request.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));
                }

                validationCodeAlreadyExists(investmentManagementDTO.getCode(), errorMessages);

                dataChangeDTO.setInputId(request.getInputId());
                dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
                dataChangeDTO.setJsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO));

                if (errorMessages.isEmpty()) {
                    dataChangeService.createChangeActionADD(dataChangeDTO, InvestmentManagement.class);
                    totalDataSuccess++;
                } else {
                    totalDataFailed++;
                    ErrorMessageInvestmentManagementDTO errorMessageDTO = new ErrorMessageInvestmentManagementDTO(investmentManagementDTO.getCode(), errorMessages);
                    errorMessageList.add(errorMessageDTO);
                }
            }
            return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }
        return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Create investment management list approve with request: {}", investmentManagementListRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            validateDataChangeIds(investmentManagementListRequest.getInvestmentManagementRequestList());

            for (InvestmentManagementDTO investmentManagementDTO : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));
                }

                if (isCodeAlreadyExists(investmentManagementDTO.getCode())) {
                    errorMessages.add("Investment Management is already taken with code: " + investmentManagementDTO.getCode());
                }

                if (!errorMessages.isEmpty()) {
                    BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                            .id(investmentManagementDTO.getDataChangeId())
                            .approveId(investmentManagementListRequest.getApproveId())
                            .approveIPAddress(investmentManagementListRequest.getApproveIPAddress())
                            .jsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO))
                            .build();
                    dataChangeService.approvalStatusIsRejected(dataChangeDTO, errorMessages);
                    totalDataFailed++;
                } else {
                    InvestmentManagement investmentManagement = createInvestmentManagementEntity(investmentManagementDTO);
                    investmentManagementRepository.save(investmentManagement);

                    BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                            .id(investmentManagementDTO.getDataChangeId())
                            .approveId(investmentManagementListRequest.getApproveId())
                            .approveIPAddress(investmentManagementListRequest.getApproveIPAddress())
                            .entityId(investmentManagement.getId().toString())
                            .jsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO))
                            .description("Successfully approve data change and save data entity")
                            .build();
                    dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                    totalDataSuccess++;
                }
            }
            return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (DataChangeException e) {
            handleDataChangeException(e);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }
        return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public UpdateInvestmentManagementListResponse updateSingle(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update investment management by id with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            InvestmentManagementDTO investmentManagementDTO = InvestmentManagementDTO.builder()
                    .id(request.getId())
                    .code(request.getCode())
                    .name(request.getName())
                    .email(request.getEmail())
                    .address1(request.getAddress1())
                    .address2(request.getAddress2())
                    .address3(request.getAddress3())
                    .address4(request.getAddress4())
                    .build();
            List<String> errorMessages = new ArrayList<>();

            Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
            }

            validationCodeAlreadyExists(investmentManagementDTO.getCode(), errorMessages);

            InvestmentManagement investmentManagement = investmentManagementRepository.findById(investmentManagementDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

            dataChangeDTO.setInputId(request.getInputId());
            dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
            dataChangeDTO.setJsonDataBefore(objectMapper.writeValueAsString(investmentManagement));
            dataChangeDTO.setJsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO));

            if (errorMessages.isEmpty()) {
                dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
                totalDataSuccess++;
            } else {
                totalDataFailed++;
                ErrorMessageInvestmentManagementDTO errorMessageDTO = new ErrorMessageInvestmentManagementDTO(investmentManagementDTO.getCode(), errorMessages);
                errorMessageList.add(errorMessageDTO);
            }
            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (DataNotFoundException e) {
            log.error("Investment Management not found: {}", e.getMessage(), e);
            errorMessageList.add(new ErrorMessageInvestmentManagementDTO(null, Collections.singletonList(e.getMessage())));
            totalDataFailed++;
            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }
        return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update investment management list with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO investmentManagementDTO : request.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
                }

                InvestmentManagement investmentManagement = investmentManagementRepository.findById(investmentManagementDTO.getId())
                        .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

                dataChangeDTO.setInputId(request.getInputId());
                dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
                dataChangeDTO.setJsonDataBefore(objectMapper.writeValueAsString(investmentManagement));
                dataChangeDTO.setJsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO));

                if (errorMessages.isEmpty()) {
                    dataChangeService.createChangeActionEDIT(dataChangeDTO, InvestmentManagement.class);
                    totalDataSuccess++;
                } else {
                    totalDataFailed++;
                    ErrorMessageInvestmentManagementDTO errorMessageDTO = new ErrorMessageInvestmentManagementDTO(investmentManagementDTO.getCode(), errorMessages);
                    errorMessageList.add(errorMessageDTO);
                }
            }
            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (DataNotFoundException e) {
            totalDataFailed = getTotalDataFailed(e, errorMessageList, totalDataFailed);
            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }
        return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public UpdateInvestmentManagementListResponse updateListApprove(UpdateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Request data update approve: {}", investmentManagementListRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO investmentManagementDTO : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();

                Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
                }

                InvestmentManagement investmentManagement = investmentManagementRepository.findByCode(investmentManagementDTO.getCode())
                        .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getCode()));

                if (!errorMessages.isEmpty()) {
                    BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                            .id(investmentManagementDTO.getDataChangeId())
                            .approveId(investmentManagementListRequest.getApproveId())
                            .approveIPAddress(investmentManagementListRequest.getApproveIPAddress())
                            .jsonDataBefore(objectMapper.writeValueAsString(investmentManagement))
                            .jsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO))
                            .build();
                    dataChangeService.approvalStatusIsRejected(dataChangeDTO, errorMessages);
                    totalDataFailed++;
                } else {
                    updateInvestmentManagement(investmentManagement, investmentManagementDTO);
                    InvestmentManagement investmentManagementSaved = investmentManagementRepository.save(investmentManagement);
                    BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                            .id(investmentManagementDTO.getDataChangeId())
                            .approveId(investmentManagementListRequest.getApproveId())
                            .approveIPAddress(investmentManagementListRequest.getApproveIPAddress())
                            .entityId(investmentManagement.getId().toString())
                            .jsonDataBefore(objectMapper.writeValueAsString(investmentManagement))
                            .jsonDataAfter(objectMapper.writeValueAsString(investmentManagementSaved))
                            .description("Successfully approve data change and update data entity")
                            .build();
                    dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                    totalDataSuccess++;
                }
            }
            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (DataNotFoundException e) {
            totalDataFailed = getTotalDataFailed(e, errorMessageList, totalDataFailed);
            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        }
        catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }
        return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public DeleteInvestmentManagementListResponse deleteSingle(DeleteInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Delete investment management by id with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            InvestmentManagement investmentManagement= investmentManagementRepository.findById(request.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + request.getId()));

            dataChangeDTO.setInputId(request.getInputId());
            dataChangeDTO.setInputIPAddress(request.getInputIPAddress());
            dataChangeDTO.setJsonDataBefore(objectMapper.writeValueAsString(investmentManagement));
            dataChangeDTO.setJsonDataAfter("");

            dataChangeService.createChangeActionDELETE(dataChangeDTO, InvestmentManagement.class);
            totalDataSuccess++;
            return new DeleteInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (DataNotFoundException e) {
            totalDataFailed = getTotalDataFailed(e, errorMessageList, totalDataFailed);
            return new DeleteInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }
        return new DeleteInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public DeleteInvestmentManagementListResponse deleteListApprove(DeleteInvestmentManagementListRequest request) {
        log.info("Request data delete approve: {}", request);
        String approveId = request.getApproveId();
        String approveIPAddress = request.getApproveIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            for (DeleteInvestmentManagementDTO deleteInvestmentManagementDTO : request.getInvestmentManagementDTOList()) {
                InvestmentManagement investmentManagement = investmentManagementRepository.findById(deleteInvestmentManagementDTO.getId())
                        .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + deleteInvestmentManagementDTO.getId()));

                log.info("Investment Management: {}", investmentManagement);

                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateDeleteInvestmentManagementUsingValidator(deleteInvestmentManagementDTO);
                errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));

                BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                        .id(deleteInvestmentManagementDTO.getDataChangeId())
                        .approveId(approveId)
                        .approveIPAddress(approveIPAddress)
                        .build();

                if (!errorMessages.isEmpty()) {
                    errorMessageList.add(new ErrorMessageInvestmentManagementDTO(deleteInvestmentManagementDTO.getId().toString(), errorMessages));
                    dataChangeService.approvalStatusIsRejected(dataChangeDTO, errorMessages);
                    totalDataFailed++;
                } else {
                    dataChangeDTO.setDescription("Successfully approve data change and delete data entity");
                    dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                    investmentManagementRepository.delete(investmentManagement);
                    totalDataSuccess++;
                }
            }
            return new DeleteInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (DataNotFoundException e) {
            totalDataFailed = getTotalDataFailed(e, errorMessageList, totalDataFailed);
            return new DeleteInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (Exception e) {
            handleGeneralError(e);
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
        return mapToDTOList(investmentManagementRepository.findAll());
    }

    public Errors validateInvestmentManagementUsingValidator(InvestmentManagementDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "investmentManagementDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private Errors validateDeleteInvestmentManagementUsingValidator(DeleteInvestmentManagementDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "deleteInvestmentManagementDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private void validationCodeAlreadyExists(String code, List<String> errorMessages) {
        if (isCodeAlreadyExists(code)) {
            errorMessages.add("Investment Management is already taken with code: " + code);
        }
    }

    private static InvestmentManagementDTO mapToDTO(InvestmentManagement investmentManagement) {
        return InvestmentManagementDTO.builder()
                .id(investmentManagement.getId())
                .code(investmentManagement.getCode())
                .name(investmentManagement.getName())
                .email(investmentManagement.getEmail())
                .address1(investmentManagement.getAddress1())
                .address2(investmentManagement.getAddress2())
                .address3(investmentManagement.getAddress3())
                .address4(investmentManagement.getAddress4())
                .build();
    }

    private static List<InvestmentManagementDTO> mapToDTOList(List<InvestmentManagement> investmentManagementList) {
        return investmentManagementList.stream()
                .map(InvestmentManagementServiceImpl::mapToDTO)
                .toList();
    }

    private InvestmentManagement createInvestmentManagementEntity(InvestmentManagementDTO investmentManagementDTO) {
        return InvestmentManagement.builder()
                .code(investmentManagementDTO.getCode())
                .name(investmentManagementDTO.getName())
                .email(investmentManagementDTO.getEmail())
                .address1(investmentManagementDTO.getAddress1())
                .address2(investmentManagementDTO.getAddress2())
                .address3(investmentManagementDTO.getAddress3())
                .address4(investmentManagementDTO.getAddress4())
                .build();
    }

    private void updateInvestmentManagement(InvestmentManagement existingInvestment, InvestmentManagementDTO updatedDTO) {
//        if (!updatedDTO.getCode().isEmpty()) {
//            existingInvestment.setCode(updatedDTO.getCode());
//        }
        if (!updatedDTO.getName().isEmpty()) {
            existingInvestment.setName(updatedDTO.getName());
        }
        if (!updatedDTO.getEmail().isEmpty()) {
            existingInvestment.setEmail(updatedDTO.getEmail());
        }
        if (!updatedDTO.getAddress1().isEmpty()) {
            existingInvestment.setAddress1(updatedDTO.getAddress1());
        }
        if (!updatedDTO.getAddress2().isEmpty()) {
            existingInvestment.setAddress2(updatedDTO.getAddress2());
        }
        if (!updatedDTO.getAddress3().isEmpty()) {
            existingInvestment.setAddress3(updatedDTO.getAddress3());
        }
        if (!updatedDTO.getAddress4().isEmpty()) {
            existingInvestment.setAddress4(updatedDTO.getAddress4());
        }
    }


    private static int getTotalDataFailed(DataNotFoundException e, List<ErrorMessageInvestmentManagementDTO> errorMessageList, int totalDataFailed) {
        log.error("Investment Management not found: {}", e.getMessage(), e);
        errorMessageList.add(new ErrorMessageInvestmentManagementDTO(null, Collections.singletonList(e.getMessage())));
        totalDataFailed++;
        return totalDataFailed;
    }

    private void handleDataChangeException(DataChangeException e) {
        log.error("Data Change exception occurred: {}", e.getMessage());
        throw new DataChangeException("Data Change exception occurred: " + e.getMessage());
    }

    private void handleJsonProcessingException(JsonProcessingException e) {
        log.error("Error processing JSON during data change logging: {}", e.getMessage(), e);
        throw new DataChangeException("Error processing JSON during data change logging", e);
    }

    private void handleGeneralError(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        throw new DataChangeException("An unexpected error occurred: " + e.getMessage());
    }

    private void validateDataChangeIds(List<InvestmentManagementDTO> investmentManagementDTOList) {
        List<Long> idDataChangeList = investmentManagementDTOList.stream()
                .map(InvestmentManagementDTO::getDataChangeId)
                .toList();

        if (!dataChangeService.areAllIdsExistInDatabase(idDataChangeList)) {
            log.info("Data Change id not found");
            throw new DataChangeException("Data Change id not found");
        }
    }

}
