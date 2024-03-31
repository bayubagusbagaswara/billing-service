package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.model.BillingCore;

import java.util.List;

public interface CoreService {

    String calculateCore1(CoreCalculateRequest request);

    String calculateCore2(CoreCalculateRequest request);

    String calculateCore3();

    String calculateCore4();

    List<BillingCore> getAll();

    String deleteAll();
}
