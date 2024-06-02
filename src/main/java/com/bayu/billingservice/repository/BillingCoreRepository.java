package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingCore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingCoreRepository extends JpaRepository<BillingCore, Long> {

    @Query(value = "SELECT * FROM billing_core WHERE customer_code = :customerCode " +
            "AND bill_category = :billingCategory " +
            "AND bill_type = :billingType " +
            "AND month = :monthName " +
            "AND year = :year", nativeQuery = true)
    Optional<BillingCore> findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(
            @Param("customerCode") String customerCode,
            @Param("billingCategory") String billingCategory,
            @Param("billingType") String billingType,
            @Param("monthName") String monthName,
            @Param("year") Integer year);

}
