package com.bayu.billingservice.service;

import com.bayu.billingservice.model.BillingTemplate;

public interface BillingTemplateService {

    BillingTemplate getByCategoryAndTypeAndSubCode(String category, String type, String subCode);
}
