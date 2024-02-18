package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.model.SfValCoreIIG;
import com.bayu.billingservice.repository.SfValCoreIIGRepository;
import com.bayu.billingservice.service.SfValCoreIIGService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
public class SfValCoreIIGServiceImpl implements SfValCoreIIGService {

    private final SfValCoreIIGRepository sfValCoreIIGRepository;

    public SfValCoreIIGServiceImpl(SfValCoreIIGRepository sfValCoreIIGRepository) {
        this.sfValCoreIIGRepository = sfValCoreIIGRepository;
    }

    @Override
    public List<SfValCoreIIG> create() {
        return null;
    }

    private BigDecimal calculateTotalMarketValue(BigDecimal totalHolding, Integer priceTrub) {
        return totalHolding.multiply(new BigDecimal(priceTrub))
                .setScale(0, RoundingMode.HALF_UP);
    }
}
