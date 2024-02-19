package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.repository.SfValCrowdfundingRepository;
import com.bayu.billingservice.service.SfValCrowdfundingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SfValCrowdfundingServiceImpl implements SfValCrowdfundingService {

    private final SfValCrowdfundingRepository sfValCrowdfundingRepository;

    public SfValCrowdfundingServiceImpl(SfValCrowdfundingRepository sfValCrowdfundingRepository) {
        this.sfValCrowdfundingRepository = sfValCrowdfundingRepository;
    }

}
