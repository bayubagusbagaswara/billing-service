package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValRgMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SfValRgMonthlyRepository extends JpaRepository<SfValRgMonthly, Long> {

    List<SfValRgMonthly> findAllByAid(String aid);

    Optional<SfValRgMonthly> findByAidAndSecurityName(String aid, String securityName);

    void deleteByMonthAndYear(String month, Integer year);

    @Modifying
    @Query(value = "DELETE FROM sf_val_rg_monthly WHERE month = :month AND year = :year", nativeQuery = true)
    void deleteByMonthAndYearNative(@Param("month") String month, @Param("year") Integer year);

}
