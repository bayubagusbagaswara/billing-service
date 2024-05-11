package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SellingAgent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellingAgentRepository extends JpaRepository<SellingAgent, Long> {

    Boolean existsByCode(String code);

//    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END "
//            + "FROM nama_tabel "
//            + "WHERE kode = :code", nativeQuery = true)
//    Boolean existsByCode(@Param("code") String code);

//    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END "
//            + "FROM nama_tabel "
//            + "WHERE kode = :code", nativeQuery = true)
//    boolean existsByCode(@Param("code") String code);
}
