package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingNumberRepository extends JpaRepository<BillingNumber, Long> {

    @Query(nativeQuery = true, value = "SELECT " +
            "MAX(sequence_number) AS latest_sequence_number " +
            "FROM billing_number " +
            "WHERE month = :month AND year = :year")
    Integer getMaxSequenceNumberByMonthAndYear(String month, int year);

    @Query(value = "SELECT * FROM billing_number " +
            "WHERE number = :billingNumber", nativeQuery = true)
    Optional<BillingNumber> findByNumber(String billingNumber);

}
