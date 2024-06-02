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
public class CoreType2Parameter {

    private String customerCode;

    private List<SkTransaction> skTransactionList;

    private BigDecimal transactionHandlingFee;

    private List<SfValRgDaily> sfValRgDailyList;

    private BigDecimal customerMinimumFee;

    private BigDecimal vatFee;
}
