package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.KSEISafekeepingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KSEISafekeepingFeeRepository extends JpaRepository<KSEISafekeepingFee, Long> {

    Optional<KSEISafekeepingFee> findByFeeAccount(@Param("feeAccount") String feeAccount);

    Optional<KSEISafekeepingFee> findByFeeAccountContainingIgnoreCase(String feeAccount);

    @Query(value = "SELECT * FROM ksei_safekeeping_fee WHERE fee_account LIKE %:feeAccount%", nativeQuery = true)
    Optional<KSEISafekeepingFee> searchByFeeAccountLike(@Param("feeAccount") String feeAccount);
}
