package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.KseiSafekeepingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KseiSafekeepingFeeRepository extends JpaRepository<KseiSafekeepingFee, Long> {

    Optional<KseiSafekeepingFee> findByFeeAccount(@Param("feeAccount") String feeAccount);

    Optional<KseiSafekeepingFee> findByFeeAccountContainingIgnoreCase(String feeAccount);

    @Query(value = "SELECT * FROM ksei_safekeeping_fee WHERE fee_account LIKE %:feeAccount%", nativeQuery = true)
    Optional<KseiSafekeepingFee> searchByFeeAccountLike(@Param("feeAccount") String feeAccount);
}
