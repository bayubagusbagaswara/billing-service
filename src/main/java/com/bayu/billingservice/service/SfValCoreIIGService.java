package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.iig.CreateSfValCoreIIGRequest;
import com.bayu.billingservice.model.SfValCoreIIG;

import java.util.List;

public interface SfValCoreIIGService {

    String create(CreateSfValCoreIIGRequest request);

    List<SfValCoreIIG> getAll();

    List<SfValCoreIIG> getAllByAidAndMonthYear(String aid, String monthYear);

}
