package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValRgMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SfValRgMonthlyRepository extends JpaRepository<SfValRgMonthly, Long> {

    List<SfValRgMonthly> findAllByAid(String aid);

    Optional<SfValRgMonthly> findByAidAndSecurityName(String aid, String securityName);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM SfValRgMonthly s WHERE s.month = :month AND s.year = :year")
    void deleteByMonthAndYear(@Param("month") String month, @Param("year") Integer year);

    @Query(value = "SELECT FROM sf_val_rg_monthly as s WHERE s.aid = :customerCode " +
            "AND s.month = :month " +
            "AND s.year = :year", nativeQuery = true)
    List<SfValRgMonthly> findAllByCustomerCodeAndMonthAndYear(
            @Param("customerCode") String customerCode,
            @Param("month") String month,
            @Param("year") Integer year);

}
