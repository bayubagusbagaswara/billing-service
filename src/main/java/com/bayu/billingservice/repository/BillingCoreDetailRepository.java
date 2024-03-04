package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingCoreDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingCoreDetailRepository extends JpaRepository<BillingCoreDetail, Long> {

    // get data 3 latest month by portfolio code
}
