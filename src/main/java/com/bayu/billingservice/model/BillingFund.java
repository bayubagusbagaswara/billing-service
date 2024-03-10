package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.BaseBilling;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing_funds")
@Data
@SuperBuilder
public class BillingFund extends BaseBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_fee")
    private BigDecimal customerFee;

    @Column(name = "accrual_custodial_fee")
    private BigDecimal accrualCustodialFee;

    @Column(name = "bis4_value_frequency")
    private int bis4ValueFrequency;

    @Column(name = "bis4_transaction_fee")
    private BigDecimal bis4TransactionFee;

    @Column(name = "bis4_amount_due")
    private BigDecimal bis4AmountDue;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @Column(name = "vat_fee")
    private BigDecimal vatFee;

    @Column(name = "vat_amount_due")
    private BigDecimal vatAmountDue;

    @Column(name = "ksei_value_frequency")
    private int kseiValueFrequency;

    @Column(name = "ksei_transaction_fee")
    private BigDecimal kseiTransactionFee;

    @Column(name = "ksei_amount_due")
    private BigDecimal kseiAmountDue;

    @Column(name = "total_amount_due")
    private BigDecimal totalAmountDue;
}
