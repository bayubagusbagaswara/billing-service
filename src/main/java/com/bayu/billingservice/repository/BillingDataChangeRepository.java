package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingDataChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingDataChangeRepository extends JpaRepository<BillingDataChange, Long> {
}
