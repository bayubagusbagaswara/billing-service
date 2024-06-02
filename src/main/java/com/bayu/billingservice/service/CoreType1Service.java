package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.core.CoreCalculateRequest;
import com.bayu.billingservice.model.BillingCore;

import java.util.List;

public interface CoreType1Service {

    BillingCalculationResponse calculate(CoreCalculateRequest request);

    List<BillingCore> getAll();

}
