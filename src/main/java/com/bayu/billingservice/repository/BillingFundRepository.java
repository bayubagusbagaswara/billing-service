package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingFund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingFundRepository extends JpaRepository<BillingFund, Long> {

    @Query(value = "SELECT * FROM billing_funds " +
            "WHERE bill_category = :billingCategory " +
            "AND month = :month " +
            "AND year = :year " +
            "AND approval_status = :approvalStatus", nativeQuery = true)
    List<BillingFund> findAllByBillingCategoryAndMonthAndYearAndApprovalStatus(
            @Param("billingCategory") String billingCategory,
            @Param("month") String month,
            @Param("year") int year,
            @Param("approvalStatus") String approvalStatus
    );

}
