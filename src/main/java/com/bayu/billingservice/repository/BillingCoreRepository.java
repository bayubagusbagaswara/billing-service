package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingCore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingCoreRepository extends JpaRepository<BillingCore, Long> {

    @Query(value = "SELECT * FROM billing_cores " +
            "WHERE bill_category = :billingCategory " +
            "AND bill_type = :billingType " +
            "AND month = :month " +
            "AND year = :year " +
            "AND approval_status = :approvalStatus", nativeQuery = true)
    List<BillingCore> findAllByBillingCategoryAndBillingTypeAndMonthAndYearAndApprovalStatus(
            @Param("billingCategory") String billingCategory,
            @Param("billingType") String billingType,
            @Param("month") String month,
            @Param("year") int year,
            @Param("approvalStatus") String approvalStatus
    );

    @Query(value = "SELECT * FROM billing_cores " +
            "WHERE aid = :aid " +
            "AND bill_category = :billingCategory " +
            "AND bill_type = :billingType " +
            "AND month = :month " +
            "AND year = :year", nativeQuery = true)
    Optional<BillingCore> findByAidAndBillingCategoryAndBillingTypeAndMonthAndYear(
            @Param("aid") String aid,
            @Param("billingCategory") String billingCategory,
            @Param("billingType") String billingType,
            @Param("month") String monthName,
            @Param("year") int year);

}
