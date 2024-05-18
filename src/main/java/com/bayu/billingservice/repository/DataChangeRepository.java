package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.DataChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DataChangeRepository extends JpaRepository<DataChange, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(*) = :idListSize THEN 1 ELSE 0 END FROM billing_data_changes WHERE id IN :idList", nativeQuery = true)
    Boolean existsByIdList(@Param("idList") List<Long> idList, @Param("idListSize") Integer idListSize);

    long countByIdIn(List<Long> idList);

    List<DataChange> findByIdIn(List<Long> idList);
}
