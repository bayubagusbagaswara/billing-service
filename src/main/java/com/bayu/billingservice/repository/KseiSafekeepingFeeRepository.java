package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.KseiSafekeepingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KseiSafekeepingFeeRepository extends JpaRepository<KseiSafekeepingFee, Long> {

    Optional<KseiSafekeepingFee> findByCustomerCodeContainingIgnoreCase(String feeAccount);

    @Query(value = "SELECT * FROM ksei_safekeeping_fee " +
            "WHERE customer_code = :customerCode " +
            "AND date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<KseiSafekeepingFee> findByCustomerCodeAndDateBetweenNative(
            @Param("customerCode") String customerCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    Optional<KseiSafekeepingFee> findByCustomerCodeAndMonthAndYear(String customerCode, String month, int year);

}
