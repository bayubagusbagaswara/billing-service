package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValRgDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SfValRgDailyRepository extends JpaRepository<SfValRgDaily, Long> {

    List<SfValRgDaily> findAllByAid(String aid);

    List<SfValRgDaily> findAllByAidAndSecurityName(String aid, String securityName);

    List<SfValRgDaily> findAllByAidAndDate(String aid, LocalDate date);

    List<SfValRgDaily> findAllByAidAndYearAndMonth(String aid, Integer year, String monthName);

}
