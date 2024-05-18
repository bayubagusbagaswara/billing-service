package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.BaseBilling;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing_core")
@Data
@SuperBuilder
@NoArgsConstructor
public class BillingCore extends BaseBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "swift_code")
//    private String swiftCode; // especially for core template 2 and 6
//
//    @Column(name = "corr_bank")
//    private String corrBank;
//
//    @Column(name = "debit_from")
//    private String debitFrom; // core type 3
//
//    @Column(name = "credit_to")
//    private String creditTo; // core type 3

//    @Column(name = "account_number_cbest")
//    private String accountNumberCBEST; // especially for core template 6

    @Column(name = "customer_minimum_fee")
    private BigDecimal customerMinimumFee; // minimum fee that customers have, 5.000.000, 500.000 etc

    @Column(name = "customer_safekeeping_fee")
    private BigDecimal customerSafekeepingFee;

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
    @Column(name = "ksei_safekepeing_amount_due")
    private BigDecimal kseiSafekeepingAmountDue;

    // For Transaction KSEI
    @Column(name = "ksei_transaction_value_frequency")
    private Integer kseiTransactionValueFrequency;

    @Column(name = "ksei_transaction_fee")
    private BigDecimal kseiTransactionFee;

    @Column(name = "ksei_transaction_amount_due")
    private BigDecimal kseiTransactionAmountDue;

    // For Transaction BI-SSSS
    @Column(name = "bis4_transaction_value_frequency")
    private Integer bis4TransactionValueFrequency;

    @Column(name = "bis4_transaction_fee")
    private BigDecimal bis4TransactionFee;

    @Column(name = "bis4_transaction_amount_due")
    private BigDecimal bis4TransactionAmountDue;

    // Especially Core Type 8 IIG, because USD
    @Column(name = "transaction_handling_item")
    private Integer transactionHandlingItem;

    @Column(name = "safekeeping_item")
    private Integer safekeepingItem;

    @Column(name = "administration_setup_item")
    private Integer administrationSetUpItem;

    @Column(name = "administration_setup_fee")
    private BigDecimal administrationSetUpFee;

    @Column(name = "administration_setup_amount_due")
    private BigDecimal administrationSetUpAmountDue;

    @Column(name = "signing_representation_item")
    private Integer signingRepresentationItem;

    @Column(name = "signing_representation_fee")
    private BigDecimal signingRepresentationFee;

    @Column(name = "signing_representation_amount_due")
    private BigDecimal signingRepresentationAmountDue;

    @Column(name = "security_agent_item")
    private Integer securityAgentItem;

    @Column(name = "security_agent_fee")
    private BigDecimal securityAgentFee;

    @Column(name = "security_agent_amount_due")
    private BigDecimal securityAgentAmountDue;

    @Column(name = "other_item")
    private Integer otherItem;

    @Column(name = "other_fee")
    private BigDecimal otherFee;

    @Column(name = "other_amount_due")
    private BigDecimal otherAmountDue;

}
