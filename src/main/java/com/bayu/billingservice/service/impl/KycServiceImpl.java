package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.model.Kyc;
import com.bayu.billingservice.repository.KycRepository;
import com.bayu.billingservice.service.KycService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KycServiceImpl implements KycService {

    private final KycRepository kycRepository;

    public KycServiceImpl(KycRepository kycRepository) {
        this.kycRepository = kycRepository;
    }

    @Override
    public Kyc create(CreateKycRequest request) {
        return null;
    }

    @Override
    public Kyc getByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        return null;
    }

}
