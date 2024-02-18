package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.Kyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycRepository extends JpaRepository<Kyc, Long> {

    Optional<Kyc> findByAid(String aid);

    Optional<Kyc> findByKseiSafeCode(String kseiSafeCode);

    Optional<Kyc> findByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
