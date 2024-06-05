package com.bayu.billingservice.dto.core;

import com.bayu.billingservice.model.SfValRgMonthly;
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
public class CoreType7Parameter {

    private BigDecimal customerSafekeepingFee;

    private List<List<SkTransaction>> skTransactionsList;

    private List<List<SfValRgMonthly>> sfValRgMonthliesList;

    private List<BigDecimal> kseiAmountFeeList;

    private BigDecimal vatFee;

    private BigDecimal kseiTransactionFee;
}
