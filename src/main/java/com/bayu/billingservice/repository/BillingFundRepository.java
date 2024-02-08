package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingFund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingFundRepository extends JpaRepository<BillingFund, Long> {
}
