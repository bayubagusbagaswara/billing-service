package com.bayu.billingservice.service;

import com.bayu.billingservice.model.BillingTemplate;

public interface BillingTemplateService {

    BillingTemplate getByCategoryAndTypeAndSubCode(String category, String type, String subCode);

    boolean isExistsByCategoryAndTypeAndCurrencyAndSubCodeAndTemplateName(String category, String type, String currency, String subCode, String templateName);

}
