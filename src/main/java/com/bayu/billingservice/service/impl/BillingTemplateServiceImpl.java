package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.BillingTemplate;
import com.bayu.billingservice.repository.BillingTemplateRepository;
import com.bayu.billingservice.service.BillingTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingTemplateServiceImpl implements BillingTemplateService {

    private final BillingTemplateRepository billingTemplateRepository;

    @Override
    public boolean isExistsByCategoryAndTypeAndSubCode(String category, String type, String subCode) {
        return billingTemplateRepository.existsByCategoryAndTypeAndSubCode(category, type, subCode);
    }

    @Override
    public BillingTemplate getByCategoryAndTypeAndSubCode(String category, String type, String subCode) {
        return billingTemplateRepository.findByCategoryAndTypeAndSubCode(category, type, subCode)
                .orElseThrow(() -> new DataNotFoundException("Billing Template not found with category: " + category + ", type: " + type + ", and sub code: " + subCode));
    }

}
