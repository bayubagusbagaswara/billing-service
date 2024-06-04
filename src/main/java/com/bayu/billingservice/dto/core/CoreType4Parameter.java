package com.bayu.billingservice.dto.core;

import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SkTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType4Parameter {

    private BigDecimal customerSafekeepingFee; // ITAMA

    private BigDecimal transactionHandlingFee; // ITAMA

    private BigDecimal vatFee; // ITAMA

    private List<SfValRgDaily> sfValRgDailyList; // ITAMA


    private List<SkTransaction> skTransactionList; // EB

    private BigDecimal kseiTransactionFee; // EB

    private BigDecimal kseiSafeFeeAmount; // EB
}
