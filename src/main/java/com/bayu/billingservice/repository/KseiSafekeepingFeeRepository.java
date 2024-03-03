package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.KseiSafekeepingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KseiSafekeepingFeeRepository extends JpaRepository<KseiSafekeepingFee, Long> {

    Optional<KseiSafekeepingFee> findByCustomerCodeContainingIgnoreCase(String feeAccount);

    List<KseiSafekeepingFee> findByCustomerCodeAndDateBetween(String customerCode, LocalDate startDate, LocalDate endDate);

    Optional<KseiSafekeepingFee> findByCustomerCodeAndMonthAndYear(String customerCode, String month, int year);

}
