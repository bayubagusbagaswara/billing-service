package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.ReportGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportGeneratorRepository extends JpaRepository<ReportGenerator, Long> {

    @Query(value = "SELECT * FROM report_generator " +
            "WHERE customer_code = :customerCode " +
            "AND billing_category = :category " +
            "AND billing_type = :type " +
            "AND currency = :currency " +
            "AND month = :month " +
            "AND year = :year", nativeQuery = true)
    List<ReportGenerator> findAllByCustomerCodeAndCategoryAndTypeAndCurrencyAndMonthAndYear(
            @Param("customerCode") String customerCode,
            @Param("category") String category,
            @Param("type") String type,
            @Param("currency") String currency,
            @Param("month") String month,
            @Param("year") Integer year
    );
}
