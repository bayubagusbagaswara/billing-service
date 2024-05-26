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

    @Query(value = "SELECT * FROM fee_parameter AS f WHERE f.fee_name = :name", nativeQuery = true)
    Optional<FeeParameter> findByName(@Param("name") String name);

    @Query(value = "SELECT * FROM fee_parameter AS f WHERE f.fee_code = :code", nativeQuery = true)
    Optional<FeeParameter> findByCode(@Param("code") String code);

    @Query(value = "SELECT * FROM fee_parameter AS f WHERE f.fee_name IN :names", nativeQuery = true)
    List<FeeParameter> findFeeParameterByNameList(@Param("names") List<String> names);


    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
        "FROM FeeParameter f WHERE f.feeCode = :code")
    boolean existsByCode(@Param("code") String code);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
        "FROM FeeParameter f WHERE f.feeName = :name")
    boolean existsByName(@Param("name") String name);
}
