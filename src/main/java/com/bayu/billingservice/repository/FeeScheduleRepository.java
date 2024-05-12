package com.bayu.billingservice.repository;

import com.bayu.billingservice.model.FeeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface FeeScheduleRepository extends JpaRepository<FeeSchedule, Long> {

//    @Query(value = "select case when :amount <= 0 then 0 else fee_amount end as fee_amount " +
//            "from billing_fee_schedule " +
//            "where :amount >= fee_schedule_min " +
//            "and :amount < fee_schedule_max", nativeQuery = true)
//    double checkFeeScheduleAndGetFeeValue(@Param("amount") BigDecimal amount);

    @Query(value = "select case when :amount <= 0 then '0' else cast(fee_amount as text) end as fee_amount " +
            "from billing_fee_schedule " +
            "where :amount >= fee_schedule_min " +
            "and :amount < fee_schedule_max", nativeQuery = true)
    BigDecimal checkFeeScheduleAndGetFeeValue(@Param("amount") BigDecimal amount);

    // Query untuk mencari feeAmount berdasarkan nilai fee di antara feeMinimum dan feeMaximum
//    @Query("SELECT fs.feeAmount FROM FeeSchedule fs " +
//            "WHERE fs.feeMinimum <= :fee AND fs.feeMaximum >= :fee")
//    Optional<BigDecimal> findFeeAmountByFeeRange(@Param("fee") BigDecimal fee);
}
