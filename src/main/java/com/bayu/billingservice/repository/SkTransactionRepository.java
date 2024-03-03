package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SkTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SkTransactionRepository extends JpaRepository<SkTransaction, Long> {

    List<SkTransaction> findAllByPortfolioCode(String portfolioCode);
    List<SkTransaction> findAllByPortfolioCodeAndSettlementSystem(String portfolioCode, String settlementSystem);

    List<SkTransaction> findAllByPortfolioCodeAndSettlementDate(String portfolioCode, LocalDate settlementDate);

    List<SkTransaction> findAllByPortfolioCodeAndMonthAndYear(String portfolioCode, String month, int year);

}
