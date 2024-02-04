package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValRgMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SfValRgMonthlyRepository extends JpaRepository<SfValRgMonthly, Long> {

    List<SfValRgMonthly> findAllByAid();
}
