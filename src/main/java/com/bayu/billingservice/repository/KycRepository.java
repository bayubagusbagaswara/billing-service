package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.KycCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycRepository extends JpaRepository<KycCustomer, Long> {

    Optional<KycCustomer> findByAid(String aid);

    Optional<KycCustomer> findByKseiSafeCode(String kseiSafeCode);

    Optional<KycCustomer> findByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
