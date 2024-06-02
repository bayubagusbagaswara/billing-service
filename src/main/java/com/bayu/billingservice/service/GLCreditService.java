package com.bayu.billingservice.service;

import com.bayu.billingservice.model.GLCredit;

public interface GLCreditService {

    GLCredit getByBillingTemplateAndGLCreditName(String billingTemplate, String glCreditName);

}
