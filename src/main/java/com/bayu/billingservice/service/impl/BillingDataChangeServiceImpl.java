package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.BillingDataChange;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.ChangeAction;
import com.bayu.billingservice.repository.BillingDataChangeRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import com.bayu.billingservice.util.ConvertDateUtil;
import com.bayu.billingservice.util.StringUtil;
import com.bayu.billingservice.util.TableNameResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingDataChangeServiceImpl implements BillingDataChangeService {

    private final BillingDataChangeRepository dataChangeRepository;
    private final ConvertDateUtil convertDateUtil;

    private static final String ID_NOT_FOUND = "Data Change not found with id: ";

    @Override
    public List<BillingDataChangeDTO> getAll() {
        return mapToDTOList(dataChangeRepository.findAll());
    }

    @Override
    public BillingDataChangeDTO getById(Long dataChangeId) {
        BillingDataChange dataChange = dataChangeRepository.findById(dataChangeId)
                .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + dataChangeId));
        return mapToDTO(dataChange);
    }

    @Override
    public void update(BillingDataChangeDTO dataChangeDTO) {
        log.info("Data Change dto: {}", dataChangeDTO);
        BillingDataChange dataChange = dataChangeRepository.findById(dataChangeDTO.getId())
                .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + dataChangeDTO.getId()));
        log.info("Data Change: {}", dataChangeDTO);

        dataChange.setApprovalStatus(dataChangeDTO.getApprovalStatus());
        dataChange.setInputId(dataChangeDTO.getInputId());
        dataChange.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        dataChange.setInputDate(dataChangeDTO.getInputDate());
        dataChange.setApproveId(dataChangeDTO.getApproveId());
        dataChange.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        dataChange.setApproveDate(dataChangeDTO.getApproveDate());
        dataChange.setDescription(dataChangeDTO.getDescription());
        if (!dataChangeDTO.getJsonDataAfter().isEmpty()) {
            dataChange.setJsonDataAfter(dataChangeDTO.getJsonDataAfter());
        }
        if (!dataChangeDTO.getJsonDataBefore().isEmpty()) {
            dataChange.setJsonDataBefore(dataChangeDTO.getJsonDataBefore());
        }

        dataChangeRepository.save(dataChange);
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
                .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + dataChangeDTO.getId()));

        billingDataChange.setApprovalStatus(ApprovalStatus.REJECTED);
        billingDataChange.setApproveId(dataChangeDTO.getApproveId());
        billingDataChange.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        billingDataChange.setApproveDate(convertDateUtil.getDate());
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
                .orElseThrow(() -> new DataNotFoundException(ID_NOT_FOUND + dataChangeDTO.getId()));

        billingDataChange.setApprovalStatus(ApprovalStatus.APPROVED);
        billingDataChange.setApproveId(dataChangeDTO.getApproveId());
        billingDataChange.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        billingDataChange.setApproveDate(convertDateUtil.getDate());
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
                .entityId(dataChangeDTO.getEntityId())
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
                .inputDate(convertDateUtil.getDate())
                .inputIPAddress(dataChangeDTO.getInputIPAddress())
                .approveId("")
                .approveDate(null)
                .approveIPAddress("")
                .changeAction(ChangeAction.DELETE)
                .entityId(dataChangeDTO.getEntityId())
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
    public Boolean existByIdList(List<Long> idList, Integer idListSize) {
        log.info("Id List: {}, Id List Size: {}", idList, idListSize);
        Boolean b = dataChangeRepository.existsByIdList(idList, idListSize);
        log.info("Status: {}", b);
        return b;
    }

    @Override
    public boolean existById(Long id) {
        return dataChangeRepository.existsById(id);
    }

    @Override
    public boolean areAllIdsExistInDatabase(List<Long> idList) {
        long countOfExistingIds = dataChangeRepository.countByIdIn(idList);
        List<BillingDataChange> existingDataChanges = dataChangeRepository.findByIdIn(idList);
        Set<Long> existingIds = existingDataChanges.stream()
                .map(BillingDataChange::getId)
                .collect(Collectors.toSet());
        Set<Long> idSet = new HashSet<>(idList);
        return existingIds.equals(idSet) && countOfExistingIds == idList.size();
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
