package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.exception.DataChangeException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.repository.BillingDataChangeRepository;
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
    private final BillingDataChangeRepository dataChangeRepository;
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
            errors.getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));

            if (isCodeAlreadyExists(investmentManagementDTO.getCode())) {
                errorMessages.add("Investment Management is already taken with code: " + investmentManagementDTO.getCode());
            }

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
            log.error("Error processing JSON during data change logging: {}" + e.getMessage(), e);
            throw new DataChangeException("Error processing JSON during data change logging", e);
        } catch (Exception e) {
            log.error("An error occurred while processing investment management records: {}" + e.getMessage(), e);
            throw new DataChangeException("An error occurred while processing investment management records", e);
        }
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
                errors.getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));

                if (isCodeAlreadyExists(investmentManagementDTO.getCode())) {
                    errorMessages.add("Investment Management is already taken with code: " + investmentManagementDTO.getCode());
                }

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
            log.error("Error processing JSON during data change logging: {}" + e.getMessage(), e);
            throw new DataChangeException("Error processing JSON during data change logging", e);
        } catch (Exception e) {
            log.error("An error occurred while processing investment management records: {}" + e.getMessage(), e);
            throw new DataChangeException("An error occurred while processing investment management records", e);
        }
    }

    @Override
    public CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Create investment management list approve with request: {}", investmentManagementListRequest);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageInvestmentManagementDTOList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO investmentManagementDTO : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                List<String> errorMessageList = validateInvestmentManagementDTO(investmentManagementDTO);

                if (isCodeAlreadyExists(investmentManagementDTO.getCode())) {
                    errorMessageList.add("Investment Management is already taken with code: " + investmentManagementDTO.getCode());
                }

                if (!errorMessageList.isEmpty()) {
                    BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                            .id(investmentManagementDTO.getDataChangeId())
                            .approveId(investmentManagementListRequest.getApproveId())
                            .approveIPAddress(investmentManagementListRequest.getApproveIPAddress())
                            .jsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO))
                            .build();
                    dataChangeService.approvalStatusIsRejected(dataChangeDTO, errorMessageList);
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
            return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageInvestmentManagementDTOList);
        } catch (JsonProcessingException e) {
            handleJsonProcessingError(e);
        } catch (Exception e) {
            e.printStackTrace();
            handleGeneralError(e);
        }
        return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageInvestmentManagementDTOList);
    }

    @Override
    public UpdateInvestmentManagementListResponse updateById(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update investment management by id with request: {}", request);
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
        return processingSingleInvestmentManagementUpdate(investmentManagementDTO, dataChangeDTO, request.getInputId(), request.getInputIPAddress());
    }

    @Override
    public UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update investment management list with request: {}", request);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageInvestmentManagementDTOList = new ArrayList<>();
        for (InvestmentManagementDTO investmentManagementDTO : request.getInvestmentManagementRequestList()) {
            UpdateInvestmentManagementListResponse response = processingSingleInvestmentManagementUpdate(investmentManagementDTO, dataChangeDTO, request.getInputId(), request.getInputIPAddress());
            totalDataSuccess += response.getTotalDataSuccess();
            totalDataFailed += response.getTotalDataFailed();
            errorMessageInvestmentManagementDTOList.addAll(response.getErrorMessages());
        }
        return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageInvestmentManagementDTOList);
    }

    private UpdateInvestmentManagementListResponse processingSingleInvestmentManagementUpdate(InvestmentManagementDTO investmentManagementDTO, BillingDataChangeDTO dataChangeDTO, String inputId, String inputIPAddress) {
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            List<String> errorMessages = validateInvestmentManagementDTO(investmentManagementDTO);
            validationCodeAlreadyExists(investmentManagementDTO.getCode(), errorMessages);
            InvestmentManagement investmentManagement = investmentManagementRepository.findById(investmentManagementDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

            dataChangeDTO.setInputId(inputId);
            dataChangeDTO.setInputIPAddress(inputIPAddress);
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
                List<String> errorMessages = validateInvestmentManagementDTO(investmentManagementDTO);
                validationCodeAlreadyExists(investmentManagementDTO.getCode(), errorMessages);

                InvestmentManagement investmentManagement = investmentManagementRepository.findById(investmentManagementDTO.getId())
                        .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + investmentManagementDTO.getId()));

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
                    investmentManagementRepository.save(investmentManagement);

                    BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                            .id(investmentManagementDTO.getDataChangeId())
                            .approveId(investmentManagementListRequest.getApproveId())
                            .approveIPAddress(investmentManagementListRequest.getApproveIPAddress())
                            .entityId(investmentManagement.getId().toString())
                            .jsonDataBefore(objectMapper.writeValueAsString(investmentManagement))
                            .jsonDataAfter(objectMapper.writeValueAsString(investmentManagementDTO))
                            .description("Successfully approve data change and update data entity")
                            .build();
                    dataChangeService.approvalStatusIsApproved(dataChangeDTO);
                    totalDataSuccess++;
                }
            }
            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (JsonProcessingException e) {
            handleJsonProcessingError(e);
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
        } catch (JsonProcessingException e) {
            handleJsonProcessingError(e);
        } catch (Exception e) {
            handleGeneralError(e);
        }
        return new DeleteInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
    }

    @Override
    public DeleteInvestmentManagementListResponse deleteListApprove(DeleteInvestmentManagementListRequest request) {
//        log.info("Request data delete approve: {}", request);
//        String approveId = request.getApproveId();
//        String approveIPAddress = request.getApproveIPAddress();
//        int totalDataSuccess = 0;
//        int totalDataFailed = 0;
//        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();
//
//        try {
//            for (InvestmentManagementDTO investmentManagementDTO : request.getInvestmentManagementDTOList()) {
//                List<String> errorMessages = new ArrayList<>();
//
//                Optional<InvestmentManagement> investmentManagementOptional = investmentManagementRepository.findById(investmentManagementDTO.getId());
//                if (investmentManagementOptional.isEmpty()) {
//                    errorMessages.add(ID_NOT_FOUND + investmentManagementDTO.getId());
//                }
//
//                BillingDataChange dataChangeEntity = getBillingDataChangeById(investmentManagementDTO.getDataChangeId());
//
//                if (errorMessages.isEmpty()) {
//                    InvestmentManagement investmentManagement = investmentManagementOptional.get();
//                    investmentManagementRepository.delete(investmentManagement);
//
//                    String jsonDataAfter = objectMapper.writeValueAsString(investmentManagementDTO);
//                    dataChangeEntity.setApprovalStatus(ApprovalStatus.APPROVED);
//                    dataChangeEntity.setApproveId(approveId);
//                    dataChangeEntity.setApproveIPAddress(approveIPAddress);
//                    dataChangeEntity.setApproveDate(new Date());
//                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);
//                    dataChangeEntity.setDescription("Successfully approve data change and delete data entity");
//
//                    dataChangeRepository.save(dataChangeEntity);
//                    totalDataSuccess++;
//                } else {
//                    String jsonDataAfter = objectMapper.writeValueAsString(investmentManagementDTO);
//                    dataChangeEntity.setApprovalStatus(ApprovalStatus.REJECTED);
//                    dataChangeEntity.setApproveId(approveId);
//                    dataChangeEntity.setApproveIPAddress(approveIPAddress);
//                    dataChangeEntity.setApproveDate(new Date());
//                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);
//                    dataChangeEntity.setDescription(StringUtil.joinStrings(errorMessages));
//
//                    dataChangeRepository.save(dataChangeEntity);
//                    totalDataFailed++;
//                }
//            }
//            return new DeleteInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
//        } catch (Exception e) {
//            log.error("An error occurred while deleting entity data investment managements: {}", e.getMessage());
//            throw new DataProcessingException("An error occurred while deleting entity data investment managements", e);
//        }
        return null;
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

    private List<String> validateInvestmentManagementDTO(InvestmentManagementDTO investmentManagementDTO) {
        Errors errors = validateInvestmentManagementUsingValidator(investmentManagementDTO);
        return errors.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
    }

    private void validationCodeAlreadyExists(String code, List<String> errorMessages) {
        if (isCodeAlreadyExists(code)) {
            errorMessages.add("Investment Management Code '" + code + "' is already taken");
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

    private void updateInvestmentManagement(InvestmentManagement existingInvestment, InvestmentManagementDTO updatedDTO) {
        // Update only the fields that are present in the updated DTO
        if (!updatedDTO.getCode().isEmpty()) {
            existingInvestment.setCode(updatedDTO.getCode());
        }
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

    // Helper method to create InvestmentManagement entity from DTO
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

    private void handleJsonProcessingError(JsonProcessingException e) {
        handleJsonProcessingException(e);
    }

//    private void handleGeneralError(Exception e, List<ErrorMessageInvestmentManagementDTO> errorMessageList) {
//        log.error("An error occurred while processing investment management records: {}", e.getMessage());
//        String errorMessage = "An error occurred: " + e.getMessage();
//        errorMessageList.add(new ErrorMessageInvestmentManagementDTO(null, Collections.singletonList(errorMessage)));
//    }

    private void handleGeneralError(Exception e) {
        log.error("An error occurred while processing investment management records: {}", e.getMessage());
        throw new DataChangeException("An error occurred while processing investment management records", e);
    }

    private static void handleJsonProcessingException(JsonProcessingException e) {
        log.error("Error processing JSON during data change logging: {}", e.getMessage());
        throw new DataChangeException("Error processing JSON during data change logging", e);
    }

}
