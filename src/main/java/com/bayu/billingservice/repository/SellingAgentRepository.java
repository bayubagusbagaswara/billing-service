package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.SellingAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SellingAgentRepository extends JpaRepository<SellingAgent, Long> {

    @Query(value = "SELECT COUNT(*) FROM selling_agent WHERE code = :code", nativeQuery = true)
    Integer countByCode(@Param("code") String code);

    boolean existsByCode(String code);

    @Query(value = """
            SELECT * FROM selling_agent WHERE code = :code
            """, nativeQuery = true)
    Optional<SellingAgent> findByCode(@Param("code") String sellingAgentCode);

}
