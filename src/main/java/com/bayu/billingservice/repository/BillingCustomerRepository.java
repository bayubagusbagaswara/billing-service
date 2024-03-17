package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingCustomerRepository extends JpaRepository<BillingCustomer, Long> {

    Optional<BillingCustomer> findByCustomerCode(String aid);

    Optional<BillingCustomer> findByKseiSafeCode(String kseiSafeCode);

    List<BillingCustomer> findByBillingCategoryAndBillingType(String billingCategory, String billingType);
}
