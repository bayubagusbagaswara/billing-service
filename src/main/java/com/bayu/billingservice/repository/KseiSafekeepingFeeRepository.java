package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.KseiSafekeepingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KseiSafekeepingFeeRepository extends JpaRepository<KseiSafekeepingFee, Long> {

    List<KseiSafekeepingFee> findByKseiSafeCodeContainingIgnoreCase(String feeAccount);

    @Query(value = "SELECT * FROM ksei_safekeeping_fee " +
            "WHERE ksei_safe_code = :kseiSafeCode " +
            "AND created_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<KseiSafekeepingFee> findByCustomerCodeAndDateBetweenNative(
            @Param("kseiSafeCode") String kseiSafeCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM KseiSafekeepingFee k WHERE k.month = :month AND k.year = :year")
    void deleteByMonthAndYear(@Param("month") String month, @Param("year") Integer year);

    @Query(value = "SELECT * FROM ksei_safekeeping_fee " +
            "WHERE ksei_safe_code = :kseiSafeCode " +
            "AND month = :month " +
            "AND year = :year " +
            "AND ksei_safe_code <> ''", nativeQuery = true)
    Optional<KseiSafekeepingFee> findByKseiSafeCodeAndMonthAndYear(
            @Param("kseiSafeCode") String kseiSafeCode,
            @Param("month") String monthName,
            @Param("year") int year
    );

}
