package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingRetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingRetailRepository extends JpaRepository<BillingRetail, Long> {
}
