package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.BillingCalculationResponse;
import com.bayu.billingservice.dto.CoreCalculateRequest;

public interface CoreType2Service {

    BillingCalculationResponse calculate(CoreCalculateRequest request);

}
