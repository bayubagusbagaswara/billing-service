package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.FeeParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeParameterRepository extends JpaRepository<FeeParameter, Long> {

    Optional<FeeParameter> findByName(String name);

    @Query(value = "SELECT * FROM fee_parameter AS f " +
            "WHERE f.name IN :names", nativeQuery = true)
    List<FeeParameter> findFeeParameterByNameList(@Param("names") List<String> names);
}
