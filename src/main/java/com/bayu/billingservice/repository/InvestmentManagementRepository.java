package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.InvestmentManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvestmentManagementRepository extends JpaRepository<InvestmentManagement, Long> {

    Optional<InvestmentManagement> findByCode(String code);

    Boolean existsByCode(String code);

//    @Modifying
//    @Query(value = "DELETE FROM  WHERE month = :month AND year = :year", nativeQuery = true)
//    void deleteByMonthAndYearNative(@Param("month") String month, @Param("year") Integer year);

}
