package com.bayu.billingservice.service;

import com.bayu.billingservice.model.BillingTemplate;

public interface BillingTemplateService {

    boolean isExistsByCategoryAndTypeAndSubCode(String category, String type, String subCode);

    BillingTemplate getByCategoryAndTypeAndSubCode(String category, String type, String subCode);
}
