package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.GLCredit;
import com.bayu.billingservice.repository.GLCreditRepository;
import com.bayu.billingservice.service.GLCreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GLCreditServiceImpl implements GLCreditService {

    private final GLCreditRepository glCreditRepository;

    @Override
    public GLCredit getByBillingTemplateAndGLCreditName(String billingTemplate, String glCreditName) {
        return glCreditRepository.findByGlBillingTemplateAndGlCreditName(billingTemplate, glCreditName)
                .orElseThrow(() -> new DataNotFoundException("Billing GL Credit not found with billing template: " + billingTemplate + ", and GL credit name: " + glCreditName));
    }
}
