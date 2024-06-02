package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.core.CoreCalculateRequest;

public interface CoreType4Service {

    BillingCalculationResponse calculate(CoreCalculateRequest request);

}
