package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.BaseBilling;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing_retail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingRetail extends BaseBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "selling_agent")
    private String sellingAgent;

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private String month;

    @Column(name = "safekeeping_fr")
    private BigDecimal safekeepingFR;

    @Column(name = "safekeeping_sr")
    private BigDecimal safekeepingSR;

    @Column(name = "safekeeping_st")
    private BigDecimal safekeepingST;

    @Column(name = "safekeeping_ori")
    private BigDecimal safekeepingORI;

    @Column(name = "safekeeping_sbr")
    private BigDecimal safekeepingSBR;

    @Column(name = "safekeeping_pbs")
    private BigDecimal safekeepingPBS;

    @Column(name = "safekeeping_corporate_bond")
    private BigDecimal safekeepingCorporateBond;

    @Column(name = "total_amount_due")
    private BigDecimal totalAmountDue;

    @Column(name = "safekeeping_value_frequency")
    private BigDecimal safekeepingValueFrequency;

    @Column(name = "safekeeping_fee")
    private BigDecimal safekeepingFee;

    @Column(name = "safekeeping_amount_due")
    private BigDecimal safekeepingAmountDue;

    @Column(name = "transaction_settlement_value_frequency")
    private Integer transactionSettlementValueFrequency;

    @Column(name = "transaction_settlement_fee")
    private BigDecimal transactionSettlementFee;

    @Column(name = "transaction_settlement_amount_due")
    private BigDecimal transactionSettlementAmountDue;

    @Column(name = "ad_hoc_report_value_frequency")
    private Integer adHocReportValueFrequency;

    @Column(name = "ad_hoc_report_fee")
    private BigDecimal adHocReportFee;

    @Column(name = "ad_hoc_report_amount_due")
    private BigDecimal adHocReportAmountDue;

    @Column(name = "third_party_value_frequency")
    private Integer thirdPartyValueFrequency;

    @Column(name = "third_party_fee")
    private BigDecimal thirdPartyFee;

    @Column(name = "third_party_amount_due")
    private BigDecimal thirdPartyAmountDue;

    @Column(name = "vat_fee")
    private BigDecimal vatFee;

    @Column(name = "vat_amount_due")
    private BigDecimal vatAmountDue;

    @Column(name = "sub_total_amount_due")
    private BigDecimal subTotalAmountDue;

    @Column(name = "transaction_handling_value_frequency")
    private Integer transactionHandlingValueFrequency;

    @Column(name = "transaction_handling_fee")
    private BigDecimal transactionHandlingFee;

    @Column(name = "transaction_handling_amount_due")
    private BigDecimal transactionHandlingAmountDue;

    @Column(name = "transaction_handling_internal_value_frequency")
    private Integer transactionHandlingInternalValueFrequency;

    @Column(name = "transaction_handling_internal_fee")
    private BigDecimal transactionHandlingInternalFee;

    @Column(name = "transaction_handling_internal_amount_due")
    private BigDecimal transactionHandlingInternalAmountDue;

    @Column(name = "transfer_value_frequency")
    private Integer transferValueFrequency;

    @Column(name = "transfer_fee")
    private BigDecimal transferFee;

    @Column(name = "transfer_amount_due")
    private BigDecimal transferAmountDue;
}
