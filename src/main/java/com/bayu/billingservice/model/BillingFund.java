package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.BaseBilling;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing_fund")
@Data
@SuperBuilder
@NoArgsConstructor
public class BillingFund extends BaseBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accrual_custodial_value_frequency")
    private BigDecimal accrualCustodialValueFrequency;

    @Column(name = "accrual_custodial_safekeeping_fee")
    private BigDecimal accrualCustodialSafekeepingFee;

    @Column(name = "accrual_custodial_fee")
    private BigDecimal accrualCustodialFee;

    @Column(name = "bis4_transaction_value_frequency")
    private Integer bis4TransactionValueFrequency;

    @Column(name = "bis4_transaction_fee")
    private BigDecimal bis4TransactionFee;

    @Column(name = "bis4_transaction_amount_due")
    private BigDecimal bis4TransactionAmountDue;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @Column(name = "vat_fee")
    private BigDecimal vatFee;

    @Column(name = "vat_amount_due")
    private BigDecimal vatAmountDue;

    @Column(name = "ksei_transaction_value_frequency")
    private Integer kseiTransactionValueFrequency;

    @Column(name = "ksei_transaction_fee")
    private BigDecimal kseiTransactionFee;

    @Column(name = "ksei_transaction_amount_due")
    private BigDecimal kseiTransactionAmountDue;

    @Column(name = "total_amount_due")
    private BigDecimal totalAmountDue;
}
