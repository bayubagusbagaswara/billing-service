package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.BillingDataChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillingDataChangeRepository extends JpaRepository<BillingDataChange, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(*) = :idListSize THEN 1 ELSE 0 END " +
            "FROM billing_data_changes WHERE id IN :idList", nativeQuery = true)
    Boolean existsByIdList(@Param("idList") List<Long> idList, @Param("idListSize") Integer idListSize);
}
