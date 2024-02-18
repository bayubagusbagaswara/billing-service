package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.exception.DataNotFoundException;
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
        log.info("Create Kyc : {}", request);
        Kyc kyc = Kyc.builder()
                .aid(request.getAid())
                .kseiSafeCode(request.getKseiSafeCode())
                .billingCategory(request.getBillingCategory())
                .billingType(request.getBillingType())
                .build();

        return kycRepository.save(kyc);
    }

    @Override
    public Kyc getByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        return kycRepository.findByBillingCategoryAndBillingType(billingCategory, billingType)
                .orElseThrow(() -> new DataNotFoundException("Kyc not found with billing category : " + billingCategory + ", and billing type : " + billingType));
    }

}
