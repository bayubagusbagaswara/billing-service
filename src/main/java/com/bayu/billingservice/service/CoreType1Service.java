package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.BillingCalculationResponse;
import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.model.BillingCore;

import java.util.List;

public interface CoreType1Service {

    BillingCalculationResponse calculate(CoreCalculateRequest request);

    List<BillingCore> getAll();

}
