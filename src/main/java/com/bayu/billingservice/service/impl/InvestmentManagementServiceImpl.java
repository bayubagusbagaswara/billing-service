package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.exception.CreateDataException;
import com.bayu.billingservice.exception.DataChangeException;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.exception.DataProcessingException;
import com.bayu.billingservice.model.BillingDataChange;
import com.bayu.billingservice.model.InvestmentManagement;
import com.bayu.billingservice.model.enumerator.ChangeAction;
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
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public InvestmentManagement getById(Long id) {
        return investmentManagementRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Investment Management not found with id: " + id));
    }

    @Override
    public CreateInvestmentManagementListResponse create(CreateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create single investment management with request: {}", request);
        String inputId = request.getInputId();
        String inputIPAddress = request.getInputIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageInvestmentManagementDTOList = new ArrayList<>();

        try {
            List<String> errorMessages = new ArrayList<>();
            InvestmentManagementDTO investmentManagementDTO = InvestmentManagementDTO.builder()
                    .code(request.getCode())
                    .name(request.getName())
                    .email(request.getEmail())
                    .address1(request.getAddress1())
                    .address2(request.getAddress2())
                    .address3(request.getAddress3())
                    .address4(request.getAddress4())
                    .build();

            Errors errors = validateInvestmentManagementDTO(investmentManagementDTO);
            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
            }

            validationCodeAlreadyExists(investmentManagementDTO, errorMessages);

            if (errorMessages.isEmpty()) {
                BillingDataChange billingDataChange = getBillingDataChangeCreate(dataChangeDTO, investmentManagementDTO, inputId, inputIPAddress);
                dataChangeRepository.save(billingDataChange);
                totalDataSuccess++;
            } else {
                totalDataFailed = getTotalDataFailed(totalDataFailed, errorMessageInvestmentManagementDTOList, investmentManagementDTO, errorMessages);
            }

            return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageInvestmentManagementDTOList);
        } catch (Exception e) {
            log.error("An error occurred while saving data changes to create investment management data: {}", e.getMessage());
            throw new DataChangeException("An error occurred while saving data changes to create investment management data", e);
        }
    }

    @Override
    public CreateInvestmentManagementListResponse createList(CreateInvestmentManagementListRequest investmentManagementListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Create investment management list with request: {}", investmentManagementListRequest);
        String inputId = investmentManagementListRequest.getInputId();
        String inputIPAddress = investmentManagementListRequest.getInputIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed= 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageInvestmentManagementDTOList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO investmentManagementDTO : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateInvestmentManagementDTO(investmentManagementDTO);

                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
                }

                validationCodeAlreadyExists(investmentManagementDTO, errorMessages);

                if (errorMessages.isEmpty()) {
                    BillingDataChange billingDataChange = getBillingDataChangeCreate(dataChangeDTO, investmentManagementDTO, inputId, inputIPAddress);
                    dataChangeRepository.save(billingDataChange);
                    totalDataSuccess++;
                } else {
                    totalDataFailed = getTotalDataFailed(totalDataFailed, errorMessageInvestmentManagementDTOList, investmentManagementDTO, errorMessages);
                }
            }

            return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageInvestmentManagementDTOList);
        } catch (Exception e) {
            log.error("An error occurred while saving data changes to create investment management data list: {}", e.getMessage());
            throw new DataChangeException("EAn error occurred while saving data changes to create investment management data list", e);
        }
    }

    @Override
    public CreateInvestmentManagementListResponse createListApprove(CreateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Create investment management list approve with request: {}", investmentManagementListRequest);
        String approveId = investmentManagementListRequest.getApproveId();
        String approveIPAddress = investmentManagementListRequest.getApproveIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO investmentManagementDTO : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateInvestmentManagementDTO(investmentManagementDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
                }
                validationCodeAlreadyExists(investmentManagementDTO, errorMessages);
                BillingDataChange dataChange = getBillingDataChangeById(investmentManagementDTO.getDataChangeId());
                if (errorMessages.isEmpty()) {
                    InvestmentManagement investmentManagement = InvestmentManagement.builder()
                            .code(investmentManagementDTO.getCode())
                            .name(investmentManagementDTO.getName())
                            .email(investmentManagementDTO.getEmail())
                            .address1(investmentManagementDTO.getAddress1())
                            .address2(investmentManagementDTO.getAddress2())
                            .address3(investmentManagementDTO.getAddress3())
                            .address4(investmentManagementDTO.getAddress4())
                            .build();
                    investmentManagementRepository.save(investmentManagement);

                    String jsonDataAfter = objectMapper.writeValueAsString(investmentManagement);
                    dataChange.setApprovalStatus(ApprovalStatus.APPROVED);
                    dataChange.setApproveId(approveId);
                    dataChange.setApproveIPAddress(approveIPAddress);
                    dataChange.setApproveDate(new Date());
                    dataChange.setJsonDataAfter(jsonDataAfter);
                    dataChange.setDescription("Successfully approve data change and save data entity");
                    dataChangeRepository.save(dataChange);
                    totalDataSuccess++;
                } else {
                    dataChange.setApprovalStatus(ApprovalStatus.REJECTED);
                    dataChange.setApproveId(approveId);
                    dataChange.setApproveIPAddress(approveIPAddress);
                    dataChange.setApproveDate(new Date());
                    dataChange.setDescription(StringUtil.joinStrings(errorMessages));
                    dataChangeRepository.save(dataChange);
                    totalDataFailed++;
                }
            }
            return new CreateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (Exception e) {
            log.error("An error occurred while saving entity data investment managements: {}", e.getMessage());
            throw new DataProcessingException("An error occurred while saving entity data investment managements", e);
        }
    }

    @Override
    public UpdateInvestmentManagementListResponse updateById(UpdateInvestmentManagementRequest request, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update investment management by id with request: {}", request);
        String inputId = request.getInputId();
        String inputIPAddress = request.getInputIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageInvestmentManagementDTOList = new ArrayList<>();

        try {
            List<String> errorMessages = new ArrayList<>();
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

            Errors errors = validateInvestmentManagementDTO(investmentManagementDTO);

            if (errors.hasErrors()) {
                errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
            }

            validationCodeAlreadyExists(investmentManagementDTO, errorMessages);

            if (errorMessages.isEmpty()) {
                InvestmentManagement investmentManagement = getById(investmentManagementDTO.getId());
                BillingDataChange billingDataChange = getBillingDataChangeUpdate(dataChangeDTO, investmentManagement, investmentManagementDTO, inputId, inputIPAddress);
                dataChangeRepository.save(billingDataChange);
                totalDataSuccess++;
            } else {
                totalDataFailed = getTotalDataFailed(totalDataFailed, errorMessageInvestmentManagementDTOList, investmentManagementDTO, errorMessages);
            }

            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageInvestmentManagementDTOList);
        } catch (Exception e) {
            log.error("An error occurred while saving data changes to update investment management single data: {}", e.getMessage());
            throw new DataChangeException("An error occurred while saving data changes to create investment management data list", e);
        }
    }

    @Override
    public UpdateInvestmentManagementListResponse updateList(UpdateInvestmentManagementListRequest investmentManagementListRequest, BillingDataChangeDTO dataChangeDTO) {
        log.info("Update investment management list with request: {}", investmentManagementListRequest);
        String inputId = investmentManagementListRequest.getInputId();
        String inputIPAddress = investmentManagementListRequest.getInputIPAddress();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageInvestmentManagementDTOList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO investmentManagementDTO : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateInvestmentManagementDTO(investmentManagementDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
                }
                validationCodeAlreadyExists(investmentManagementDTO, errorMessages);
                if (errorMessages.isEmpty()) {
                    Long id = investmentManagementDTO.getId();
                    InvestmentManagement investmentManagement = getById(id);
                    BillingDataChange dataChange = getBillingDataChangeUpdate(dataChangeDTO, investmentManagement, investmentManagementDTO, inputId, inputIPAddress);
                    dataChangeRepository.save(dataChange);
                    totalDataSuccess++;
                } else {
                    totalDataFailed = getTotalDataFailed(totalDataFailed, errorMessageInvestmentManagementDTOList, investmentManagementDTO, errorMessages);
                }
            }
            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageInvestmentManagementDTOList);
        } catch (Exception e) {
            log.error("An error occurred while saving data changes to update investment management list data: {}", e.getMessage());
            throw new CreateDataException("An error occurred while saving data changes to update investment management list data", e);
        }
    }

    @Override
    public UpdateInvestmentManagementListResponse updateListApprove(UpdateInvestmentManagementListRequest investmentManagementListRequest) {
        log.info("Request data update approve: {}", investmentManagementListRequest);
        String approveId = investmentManagementListRequest.getApproveId();
        String approveIPAddress = investmentManagementListRequest.getApproveIPAddress();
        Date approveDate = new Date();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageInvestmentManagementDTO> errorMessageList = new ArrayList<>();

        try {
            for (InvestmentManagementDTO investmentManagementDTO : investmentManagementListRequest.getInvestmentManagementRequestList()) {
                List<String> errorMessages = new ArrayList<>();
                Errors errors = validateInvestmentManagementDTO(investmentManagementDTO);
                if (errors.hasErrors()) {
                    errors.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
                }
                validationCodeAlreadyExists(investmentManagementDTO, errorMessages);

                BillingDataChange dataChangeEntity = getBillingDataChangeById(investmentManagementDTO.getDataChangeId());

                if (errorMessages.isEmpty()) {
                    InvestmentManagement investmentManagementEntity = getById(investmentManagementDTO.getId());
                    investmentManagementEntity.setCode(investmentManagementDTO.getCode());
                    investmentManagementEntity.setName(investmentManagementDTO.getName());
                    investmentManagementEntity.setEmail(investmentManagementDTO.getEmail());
                    investmentManagementEntity.setAddress1(investmentManagementDTO.getAddress1());
                    investmentManagementEntity.setAddress2(investmentManagementDTO.getAddress2());
                    investmentManagementEntity.setAddress3(investmentManagementDTO.getAddress3());
                    investmentManagementEntity.setAddress4(investmentManagementDTO.getAddress4());
                    InvestmentManagement investmentManagementSaved = investmentManagementRepository.save(investmentManagementEntity);

                    String jsonDataAfter = objectMapper.writeValueAsString(investmentManagementSaved);
                    dataChangeEntity.setApprovalStatus(ApprovalStatus.APPROVED);
                    dataChangeEntity.setApproveId(approveId);
                    dataChangeEntity.setApproveIPAddress(approveIPAddress);
                    dataChangeEntity.setApproveDate(approveDate);
                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);
                    dataChangeEntity.setDescription("Successfully approve data change and update data entity");

                    dataChangeRepository.save(dataChangeEntity);
                    totalDataSuccess++;
                } else {
                    String jsonDataAfter = objectMapper.writeValueAsString(investmentManagementDTO);
                    dataChangeEntity.setApprovalStatus(ApprovalStatus.REJECTED);
                    dataChangeEntity.setApproveId(approveId);
                    dataChangeEntity.setApproveIPAddress(approveIPAddress);
                    dataChangeEntity.setApproveDate(approveDate);
                    dataChangeEntity.setJsonDataAfter(jsonDataAfter);
                    dataChangeEntity.setDescription(StringUtil.joinStrings(errorMessages));

                    dataChangeRepository.save(dataChangeEntity);
                    totalDataFailed++;
                }
            }
            return new UpdateInvestmentManagementListResponse(totalDataSuccess, totalDataFailed, errorMessageList);
        } catch (Exception e) {
            log.error("An error occurred while updating entity data investment managements: {}", e.getMessage());
            throw new DataProcessingException("An error occurred while updating entity data investment managements", e);
        }
    }

    public Errors validateInvestmentManagementDTO(InvestmentManagementDTO dto) {
        Errors errors = new BeanPropertyBindingResult(dto, "investmentManagementDTO");
        validator.validate(dto, errors);
        return errors;
    }

    private BillingDataChange getBillingDataChangeCreate(BillingDataChangeDTO dataChangeDTO, InvestmentManagementDTO investmentManagementDTO, String inputId, String inputIPAddress) throws JsonProcessingException {
        String jsonDataAfter = objectMapper.writeValueAsString(investmentManagementDTO);
        return BillingDataChange.builder()
                .approvalStatus(ApprovalStatus.PENDING)
                .inputId(inputId)
                .inputDate(new Date())
                .inputIPAddress(inputIPAddress)
                .approveId("")
                .approveDate(null)
                .approveIPAddress("")
                .changeAction(ChangeAction.ADD)
                .entityId("")
                .entityClassName(InvestmentManagement.class.getName())
                .tableName(TableNameResolver.getTableName(InvestmentManagement.class))
                .jsonDataBefore("")
                .jsonDataAfter(jsonDataAfter)
                .description("")
                .methodHttp(dataChangeDTO.getMethodHttp())
                .endpoint(dataChangeDTO.getEndpoint())
                .isRequestBody(dataChangeDTO.getIsRequestBody())
                .isRequestParam(dataChangeDTO.getIsRequestParam())
                .isPathVariable(dataChangeDTO.getIsPathVariable())
                .menu(dataChangeDTO.getMenu())
                .build();
    }

    private int getTotalDataFailed(int totalDataFailed, List<ErrorMessageInvestmentManagementDTO> errorMessageInvestmentManagementDTOList, InvestmentManagementDTO investmentManagementDTO, List<String> errorMessages) {
        ErrorMessageInvestmentManagementDTO errorMessageDTO = new ErrorMessageInvestmentManagementDTO();
        errorMessageDTO.setCode(investmentManagementDTO.getCode());
        errorMessageDTO.setErrorMessages(errorMessages);
        errorMessageInvestmentManagementDTOList.add(errorMessageDTO);
        totalDataFailed++;
        return totalDataFailed;
    }

    private BillingDataChange getBillingDataChangeById(Long dataChangeId) {
        return dataChangeRepository.findById(dataChangeId)
                .orElseThrow(() -> new DataNotFoundException("Data Change not found with id: " + dataChangeId));
    }

    private void validationCodeAlreadyExists(InvestmentManagementDTO dto, List<String> errorMessages) {
        if (isCodeAlreadyExists(dto.getCode())) {
            errorMessages.add("Code '" + dto.getCode() + "' is already taken");
        }
    }

    private BillingDataChange getBillingDataChangeUpdate(BillingDataChangeDTO dataChangeDTO, InvestmentManagement investmentManagement, InvestmentManagementDTO investmentManagementDTO, String inputId, String inputIPAddress) throws JsonProcessingException {
        Long id = investmentManagement.getId();
        String jsonDataBefore = objectMapper.writeValueAsString(investmentManagement);
        String jsonDataAfter = objectMapper.writeValueAsString(investmentManagementDTO);
        return BillingDataChange.builder()
                .approvalStatus(ApprovalStatus.PENDING)
                .inputId(inputId)
                .inputDate(new Date())
                .inputIPAddress(inputIPAddress)
                .approveId("")
                .approveDate(null)
                .approveIPAddress("")
                .changeAction(ChangeAction.EDIT)
                .entityId(id.toString())
                .entityClassName(InvestmentManagement.class.getName())
                .tableName(TableNameResolver.getTableName(InvestmentManagement.class))
                .jsonDataBefore(jsonDataBefore)
                .jsonDataAfter(jsonDataAfter)
                .description("")
                .methodHttp(dataChangeDTO.getMethodHttp())
                .endpoint(dataChangeDTO.getEndpoint())
                .isRequestBody(dataChangeDTO.getIsRequestBody())
                .isRequestParam(dataChangeDTO.getIsRequestParam())
                .isPathVariable(dataChangeDTO.getIsPathVariable())
                .menu(dataChangeDTO.getMenu())
                .build();
    }

}
