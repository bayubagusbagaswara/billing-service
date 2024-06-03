package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingFund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingFundRepository extends JpaRepository<BillingFund, Long> {

    @Query(value = "SELECT * FROM billing_fund " +
            "WHERE customer_code = :customerCode " +
            "AND bill_category = :billingCategory " +
            "AND bill_type = :billingType " +
            "AND month = :month " +
            "AND year = :year", nativeQuery = true)
    Optional<BillingFund> findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYear(
            @Param("customerCode") String customerCode,
            @Param("billingCategory") String billingCategory,
            @Param("billingType") String billingType,
            @Param("month") String monthName,
            @Param("year") int year
    );

    @Query(value = "SELECT * FROM billing_fund WHERE customer_code = :customerCode " +
            "AND bill_category = :billingCategory " +
            "AND bill_type = :billingType " +
            "AND month = :month " +
            "AND year = :year " +
            "AND paid = false", nativeQuery = true)
    Optional<BillingFund> findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYearAndPaidIsFalse(
            @Param("customerCode") String customerCode,
            @Param("billingCategory") String billingCategory,
            @Param("billingType") String billingType,
            @Param("month") String month,
            @Param("year") Integer year);

    @Query(value = "SELECT * FROM billing_fund WHERE customer_code = :customerCode " +
            "AND bill_category = :billingCategory " +
            "AND bill_type = :billingType " +
            "AND month = :month " +
            "AND year = :year " +
            "AND paid = true", nativeQuery = true)
    Optional<BillingFund> findByCustomerCodeAndBillingCategoryAndBillingTypeAndMonthAndYearAndPaidIsTrue(
            @Param("customerCode") String customerCode,
            @Param("billingCategory") String billingCategory,
            @Param("billingType") String billingType,
            @Param("month") String month,
            @Param("year") Integer year);

    @Query(value = "SELECT * FROM billing_fund " +
            "WHERE bill_category = :billingCategory " +
            "AND month = :month " +
            "AND year = :year " +
            "AND approval_status = :approvalStatus", nativeQuery = true)
    List<BillingFund> findAllByBillingCategoryAndMonthAndYearAndApprovalStatus(
            @Param("billingCategory") String category,
            @Param("month") String month,
            @Param("year") int year,
            @Param("approvalStatus") String approvalStatus);

}
