package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "billings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period")
    private String period;

    @Column(name = "accrual_custodial_fee")
    private BigDecimal accrualCustodialFee;

    @Column(name = "value_frequency_s4")
    private Integer valueFrequencyS4;

    @Column(name = "amount_s4")
    private BigDecimal amountS4;

    @Column(name = "total_nominal_before_tax")
    private BigDecimal totalNominalBeforeTax;

    @Column(name = "tax_fee")
    private Double taxFee;

    @Column(name = "amount_tax")
    private BigDecimal amountTax;

    @Column(name = "value_frequency_ksei")
    private Integer valueFrequencyKSEI;

    @Column(name = "amount_ksei")
    private BigDecimal amountKSEI;

    @Column(name = "total_nominal_after_tax")
    private BigDecimal totalNominalAfterTax;

}
