package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;

import java.util.List;

public interface DataChangeService {

    List<BillingDataChangeDTO> getAll();

    String deleteAll();

    <T> void createChangeActionADD(BillingDataChangeDTO dataChangeDTO, Class<T> clazz);

    void approvalStatusIsRejected(BillingDataChangeDTO dataChangeDTO, List<String> errorMessageList);

    void approvalStatusIsApproved(BillingDataChangeDTO dataChangeDTO);

    <T> void createChangeActionEDIT(BillingDataChangeDTO dataChangeDTO, Class<T> clazz);

    <T> void createChangeActionDELETE(BillingDataChangeDTO dataChangeDTO, Class<T> clazz);

    Boolean existByIdList(List<Long> idList, Integer idListSize);

    boolean existById(Long id);

    boolean areAllIdsExistInDatabase(List<Long> idList);

    BillingDataChangeDTO getById(Long dataChangeId);

    void update(BillingDataChangeDTO dataChangeDTO);
}
