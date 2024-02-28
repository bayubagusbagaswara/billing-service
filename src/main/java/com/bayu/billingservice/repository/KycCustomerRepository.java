package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.KycCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KycCustomerRepository extends JpaRepository<KycCustomer, Long> {

    Optional<KycCustomer> findByAid(String aid);

    Optional<KycCustomer> findByKseiSafeCode(String kseiSafeCode);

    List<KycCustomer> findByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
