package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * type 3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreTemplate7 {

    private String accountCostCenterDebit;

    private BigDecimal safekeepingValueFrequency;

    private BigDecimal safekeepingFee;

    private BigDecimal safekeepingAmountDue;

    private String safekeepingJournal;

}
