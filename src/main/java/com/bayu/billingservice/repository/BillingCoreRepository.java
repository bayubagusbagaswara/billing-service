package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingCore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingCoreRepository extends JpaRepository<BillingCore, Long> {
}
