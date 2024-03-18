package com.bayu.billingservice.util;

import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.repository.BillingCoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingCoreSaveListUtil {

    private final BillingCoreRepository billingCoreRepository;

    public int saveAllEntities(List<BillingCore> billingCoreList) {
        final int batchSize = 100; // Jumlah entitas dalam satu batch
        int totalSavedEntities = 0;

        for (int i = 0; i < billingCoreList.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, billingCoreList.size());
            List<BillingCore> batchEntities = billingCoreList.subList(i, endIndex);
            List<BillingCore> savedBatchEntities = billingCoreRepository.saveAll(batchEntities);
            totalSavedEntities += savedBatchEntities.size();
        }

        return totalSavedEntities;
    }

}
