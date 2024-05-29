package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SfValCrowdfunding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SfValCrowdfundingRepository extends JpaRepository<SfValCrowdfunding, Long> {

    @Query(value = "SELECT * FROM sf_val_crowdfunding " +
            "WHERE client_code = :clientCode", nativeQuery = true)
    List<SfValCrowdfunding> findAllByClientCode(@Param("clientCode") String clientCode);


    @Query(value = "SELECT * FROM sf_val_crowdfunding " +
            "WHERE client_code = :clientCode " +
            "AND month = :month " +
            "AND year = :year", nativeQuery = true)
    List<SfValCrowdfunding> findAllByClientCodeAndMonthAndYear(
            @Param("clientCode") String clientCode,
            @Param("month") String month,
            @Param("year") Integer year
    );

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM SfValCrowdfunding s WHERE s.month = :month AND s.year = :year")
    void deleteByMonthAndYear(@Param("month") String month, @Param("year") Integer year);
}
