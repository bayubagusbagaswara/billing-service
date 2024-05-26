package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByCurrency(String currency);

    @Query(value = "SELECT e FROM ExchangeRate e WHERE e.currency = ?1 ORDER BY e.date DESC", nativeQuery = true)
    Optional<ExchangeRate> findLatestExchangeRateByCurrency(String currency);

    @Query(value = "SELECT e FROM ExchangeRate e ORDER BY e.date DESC", nativeQuery = true)
    Optional<ExchangeRate> findLatestExchangeRate();

    boolean existsByCurrency(String currency);
}
