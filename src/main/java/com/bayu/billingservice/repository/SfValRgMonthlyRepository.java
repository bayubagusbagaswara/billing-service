package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValRgMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SfValRgMonthlyRepository extends JpaRepository<SfValRgMonthly, Long> {

    List<SfValRgMonthly> findAllByAid(String aid);

    Optional<SfValRgMonthly> findByAidAndSecurityName(String aid, String securityName);
}
