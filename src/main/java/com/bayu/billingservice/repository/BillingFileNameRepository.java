package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingFileName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingFileNameRepository extends JpaRepository<BillingFileName, Long> {
}
