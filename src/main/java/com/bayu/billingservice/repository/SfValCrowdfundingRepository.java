package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValCrowdfunding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SfValCrowdfundingRepository extends JpaRepository<SfValCrowdfunding, Long> {

}
