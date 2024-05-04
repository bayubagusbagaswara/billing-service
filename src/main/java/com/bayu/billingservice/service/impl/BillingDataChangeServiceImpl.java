package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.BillingDataChange;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.ChangeAction;
import com.bayu.billingservice.repository.BillingDataChangeRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
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
    public <T> BillingDataChangeDTO createChangeActionADD(BillingDataChangeDTO dataChangeDTO, Class<T> clazz) {
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
        return mapToDTO(dataChangeRepository.save(dataChange));
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
