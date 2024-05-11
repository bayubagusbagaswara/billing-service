package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SellingAgent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellingAgentRepository extends JpaRepository<SellingAgent, Long> {

    Boolean existsByCustomerCode(String code);

}
