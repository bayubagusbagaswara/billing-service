package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.BillingDataChange;

import java.util.List;

public interface BillingDataChangeService {

    List<BillingDataChange> getAll();

    String deleteAll();

    <T> BillingDataChangeDTO createIsActionADD(BillingDataChangeDTO dataChangeDTO, Class<T> clazz);

}
