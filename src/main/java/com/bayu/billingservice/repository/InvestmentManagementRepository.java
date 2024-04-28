package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.InvestmentManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestmentManagementRepository extends JpaRepository<InvestmentManagement, Long> {

    Optional<InvestmentManagement> findByCode(String code);

    Boolean existsByCode(String code);
}
