package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.BaseBilling;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing_cores")
@Data
@SuperBuilder
@NoArgsConstructor
public class BillingCore extends BaseBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "swift_code")
    private String swiftCode; // especially for core template 2 and 6

    @Column(name = "corr_bank")
    private String corrBank;

    @Column(name = "debit_from")
    private String debitFrom; // core type 3

    @Column(name = "credit_to")
    private String creditTo; // core type 3

    @Column(name = "account_number_cbest")
    private String accountNumberCBEST; // especially for core template 6

    @Column(name = "minimum_fee")
    private BigDecimal minimumFee; // minimum fee that customers have, 5.000.000, 500.000 etc

    @Column(name = "safekeeping_journal")
    private String safekeepingJournal; // especially for core template 7

    @Column(name = "transaction_handling_journal")
    private String transactionHandlingJournal; // for core template 7 and 8

    @Column(name = "transaction_handling_value_frequency")
    private Integer transactionHandlingValueFrequency;

    @Column(name = "transaction_handling_fee")
    private BigDecimal transactionHandlingFee;

    @Column(name = "transaction_handling_amount_due")
    private BigDecimal transactionHandlingAmountDue;

    @Column(name = "safekeeping_value_frequency")
    private BigDecimal safekeepingValueFrequency;

    @Column(name = "safekeeping_fee")
    private BigDecimal safekeepingFee; // diambil dari nilai customer fee KYC

    @Column(name = "safekeeping_amount_due")
    private BigDecimal safekeepingAmountDue;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @Column(name = "vat_fee")
    private BigDecimal vatFee;

    @Column(name = "vat_amount_due")
    private BigDecimal vatAmountDue;

    @Column(name = "total_amount_due")
    private BigDecimal totalAmountDue;

    // KSEI Safe Fee Amount
    @Column(name = "ksei_safekeeping_amount_due")
    private BigDecimal kseiSafekeepingAmountDue;

    // For Transaction KSEI
    @Column(name = "ksei_transaction_value_frequency")
    private Integer kseiTransactionValueFrequency;

    @Column(name = "ksei_transaction_fee")
    private BigDecimal kseiTransactionFee;

    @Column(name = "ksei_transaction_amount_due")
    private BigDecimal kseiTransactionAmountDue;

    // For Transaction BI-SSS
    @Column(name = "bis4_transaction_value_frequency")
    private String bis4TransactionValueFrequency;

    @Column(name = "bis4_transaction_fee")
    private String bis4TransactionFee;

    @Column(name = "bis4_transaction_amount_due")
    private String bis4TransactionAmountDue;

    // Especially Core Type 8 IIG, because USD
    @Column(name = "administration_setup_item")
    private BigDecimal administrationSetUpItem;

    @Column(name = "administration_setup_fee")
    private String administrationSetUpFee;

    @Column(name = "administration_setup_amount_due")
    private String administrationAmountDue;

    @Column(name = "signing_representation_item")
    private String signingRepresentationItem;

    @Column(name = "signing_representation_fee")
    private String signingRepresentationFee;

    @Column(name = "signing_representation_amount_due")
    private String signingRepresentationAmountDue;

    @Column(name = "security_agent_item")
    private String securityAgentItem;

    @Column(name = "security_agent_fee")
    private String securityAgentFee;

    @Column(name = "security_agent_amount_due")
    private String securityAgentAmountDue;

    @Column(name = "other_item")
    private String otherItem;

    @Column(name = "other_fee")
    private String otherFee;

    @Column(name = "other_amount_due")
    private String otherAmountDue;

}
