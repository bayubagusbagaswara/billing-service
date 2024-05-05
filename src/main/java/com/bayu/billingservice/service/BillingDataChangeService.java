package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.BillingDataChange;
import com.bayu.billingservice.model.InvestmentManagement;

import java.util.List;

public interface BillingDataChangeService {

    List<BillingDataChange> getAll();

    String deleteAll();

    <T> void createChangeActionADD(BillingDataChangeDTO dataChangeDTO, Class<T> clazz);

    void approvalStatusIsRejected(BillingDataChangeDTO dataChangeDTO, List<String> errorMessageList);

    void approvalStatusIsApproved(BillingDataChangeDTO dataChangeDTO);

    <T> void createChangeActionEDIT(BillingDataChangeDTO dataChangeDTO, Class<T> clazz);

    <T> void createChangeActionDELETE(BillingDataChangeDTO dataChangeDTO, Class<T> clazz);

    boolean existByIdList(List<Long> idList, Integer idListSize);
}
