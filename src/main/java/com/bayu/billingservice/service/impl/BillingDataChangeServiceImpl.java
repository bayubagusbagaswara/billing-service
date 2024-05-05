package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.BillingDataChange;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.ChangeAction;
import com.bayu.billingservice.repository.BillingDataChangeRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.util.StringUtil;
import com.bayu.billingservice.util.TableNameResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingDataChangeServiceImpl implements BillingDataChangeService {

    private final BillingDataChangeRepository dataChangeRepository;

    @Override
    public List<BillingDataChange> getAll() {
        return dataChangeRepository.findAll();
    }

    @Override
    public String deleteAll() {
        dataChangeRepository.deleteAll();
        return "Successfully delete all data change";
    }

    @Override
    public <T> void createChangeActionADD(BillingDataChangeDTO dataChangeDTO, Class<T> clazz) {
        BillingDataChange dataChange = BillingDataChange.builder()
                .approvalStatus(ApprovalStatus.PENDING)
                .inputId(dataChangeDTO.getInputId())
                .inputDate(new Date())
                .inputIPAddress(dataChangeDTO.getInputIPAddress())
                .approveId("")
                .approveDate(null)
                .approveIPAddress("")
                .changeAction(ChangeAction.ADD)
                .entityId("")
                .entityClassName(clazz.getName())
                .tableName(TableNameResolver.getTableName(clazz))
                .jsonDataBefore("")
                .jsonDataAfter(dataChangeDTO.getJsonDataAfter())
                .description("")
                .methodHttp(dataChangeDTO.getMethodHttp())
                .endpoint(dataChangeDTO.getEndpoint())
                .isRequestBody(dataChangeDTO.getIsRequestBody())
                .isRequestParam(dataChangeDTO.getIsRequestParam())
                .isPathVariable(dataChangeDTO.getIsPathVariable())
                .menu(dataChangeDTO.getMenu())
                .build();
        dataChangeRepository.save(dataChange);
    }

    @Override
    public void approvalStatusIsRejected(BillingDataChangeDTO dataChangeDTO, List<String> errorMessageList) {
        BillingDataChange billingDataChange = dataChangeRepository.findById(dataChangeDTO.getId())
                .orElseThrow(() -> new DataNotFoundException("Data Change not found with id: " + dataChangeDTO.getId()));

        billingDataChange.setApprovalStatus(ApprovalStatus.REJECTED);
        billingDataChange.setApproveId(dataChangeDTO.getApproveId());
        billingDataChange.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        billingDataChange.setApproveDate(new Date());
        billingDataChange.setJsonDataAfter(dataChangeDTO.getJsonDataAfter() == null ? "" : dataChangeDTO.getJsonDataAfter());
        billingDataChange.setJsonDataBefore(dataChangeDTO.getJsonDataBefore() == null ? "" : dataChangeDTO.getJsonDataBefore());
        billingDataChange.setEntityId(dataChangeDTO.getEntityId() == null ? "" : dataChangeDTO.getEntityId());
        billingDataChange.setDescription(StringUtil.joinStrings(errorMessageList));

        dataChangeRepository.save(billingDataChange);
    }

    @Override
    public void approvalStatusIsApproved(BillingDataChangeDTO dataChangeDTO) {
        log.info("Approval Status Is Approved request: {}", dataChangeDTO);
        BillingDataChange billingDataChange = dataChangeRepository.findById(dataChangeDTO.getId())
                .orElseThrow(() -> new DataNotFoundException("Data Change not found with id: " + dataChangeDTO.getId()));

        billingDataChange.setApprovalStatus(ApprovalStatus.APPROVED);
        billingDataChange.setApproveId(dataChangeDTO.getApproveId());
        billingDataChange.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        billingDataChange.setApproveDate(new Date());
        billingDataChange.setJsonDataAfter(dataChangeDTO.getJsonDataAfter() == null ? "" : dataChangeDTO.getJsonDataAfter());
        billingDataChange.setJsonDataBefore(dataChangeDTO.getJsonDataBefore() == null ? "" : dataChangeDTO.getJsonDataBefore());
        billingDataChange.setEntityId(dataChangeDTO.getEntityId());
        billingDataChange.setDescription(dataChangeDTO.getDescription());

        dataChangeRepository.save(billingDataChange);
    }

    @Override
    public <T> void createChangeActionEDIT(BillingDataChangeDTO dataChangeDTO, Class<T> clazz) {
        BillingDataChange dataChange = BillingDataChange.builder()
                .approvalStatus(ApprovalStatus.PENDING)
                .inputId(dataChangeDTO.getInputId())
                .inputDate(new Date())
                .inputIPAddress(dataChangeDTO.getInputIPAddress())
                .approveId("")
                .approveDate(null)
                .approveIPAddress("")
                .changeAction(ChangeAction.EDIT)
                .entityId("")
                .entityClassName(clazz.getName())
                .tableName(TableNameResolver.getTableName(clazz))
                .jsonDataBefore(dataChangeDTO.getJsonDataBefore())
                .jsonDataAfter(dataChangeDTO.getJsonDataAfter())
                .description("")
                .methodHttp(dataChangeDTO.getMethodHttp())
                .endpoint(dataChangeDTO.getEndpoint())
                .isRequestBody(dataChangeDTO.getIsRequestBody())
                .isRequestParam(dataChangeDTO.getIsRequestParam())
                .isPathVariable(dataChangeDTO.getIsPathVariable())
                .menu(dataChangeDTO.getMenu())
                .build();
        dataChangeRepository.save(dataChange);
    }

    @Override
    public <T> void createChangeActionDELETE(BillingDataChangeDTO dataChangeDTO, Class<T> clazz) {
        BillingDataChange dataChange = BillingDataChange.builder()
                .approvalStatus(ApprovalStatus.PENDING)
                .inputId(dataChangeDTO.getInputId())
                .inputDate(new Date())
                .inputIPAddress(dataChangeDTO.getInputIPAddress())
                .approveId("")
                .approveDate(null)
                .approveIPAddress("")
                .changeAction(ChangeAction.DELETE)
                .entityId("")
                .entityClassName(clazz.getName())
                .tableName(TableNameResolver.getTableName(clazz))
                .jsonDataBefore(dataChangeDTO.getJsonDataBefore())
                .jsonDataAfter(dataChangeDTO.getJsonDataAfter())
                .description("")
                .methodHttp(dataChangeDTO.getMethodHttp())
                .endpoint(dataChangeDTO.getEndpoint())
                .isRequestBody(dataChangeDTO.getIsRequestBody())
                .isRequestParam(dataChangeDTO.getIsRequestParam())
                .isPathVariable(dataChangeDTO.getIsPathVariable())
                .menu(dataChangeDTO.getMenu())
                .build();
        dataChangeRepository.save(dataChange);
    }

    private static BillingDataChangeDTO mapToDTO(BillingDataChange dataChange) {
        return BillingDataChangeDTO.builder()
                .id(dataChange.getId())
                .approvalStatus(dataChange.getApprovalStatus())
                .inputId(dataChange.getInputId())
                .inputIPAddress(dataChange.getInputIPAddress())
                .inputDate(dataChange.getInputDate())
                .approveId(dataChange.getApproveId())
                .approveIPAddress(dataChange.getApproveIPAddress())
                .approveDate(dataChange.getApproveDate())
                .changeAction(dataChange.getChangeAction())
                .entityId(dataChange.getEntityId())
                .entityClassName(dataChange.getEntityClassName())
                .tableName(dataChange.getTableName())
                .jsonDataBefore(dataChange.getJsonDataBefore())
                .jsonDataAfter(dataChange.getJsonDataAfter())
                .description(dataChange.getDescription())
                .methodHttp(dataChange.getMethodHttp())
                .endpoint(dataChange.getEndpoint())
                .isRequestBody(dataChange.getIsRequestBody())
                .isRequestParam(dataChange.getIsRequestParam())
                .isPathVariable(dataChange.getIsPathVariable())
                .menu(dataChange.getMenu())
                .build();
    }

    private static List<BillingDataChangeDTO> mapToDTOList(List<BillingDataChange> dataChangeList) {
        return dataChangeList.stream()
                .map(BillingDataChangeServiceImpl::mapToDTO)
                .toList();
    }

}
