package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValRgDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SfValRgDailyRepository extends JpaRepository<SfValRgDaily, Long> {

    @Query(value = "SELECT * FROM sf_val_rg_daily WHERE aid = :aid", nativeQuery = true)
    List<SfValRgDaily> findAllByAid(@Param("aid") String aid);

    List<SfValRgDaily> findAllByAidAndSecurityName();

    List<SfValRgDaily> findAllByAidAndDate();
}
