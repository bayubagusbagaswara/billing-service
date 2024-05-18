package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.Approval;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing_customer")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "sub_code")
    private String subCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "billing_category")
    private String billingCategory;

    @Column(name = "billing_type")
    private String billingType;

    @Column(name = "billing_template")
    private String billingTemplate;

    @Column(name = "currency")
    private String currency;

    @Column(name = "mi_code")
    private String investmentManagementCode;

    @Column(name = "mi_name")
    private String investmentManagementName;

    @Column(name = "account")
    private String account;

    @Column(name = "cost_center_debit")
    private String costCenterDebit;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "gl_account_hasil")
    private String glAccountHasil;

    @Column(name = "customer_minimum_fee")
    private BigDecimal customerMinimumFee;

    @Column(name = "customer_safekeeping_fee")
    private BigDecimal customerSafekeepingFee;

    @Column(name = "customer_transaction_handling")
    private BigDecimal customerTransactionHandling;

    @Column(name = "npwp_number")
    private String npwpNumber;

    @Column(name = "npwp_name")
    private String npwpName;

    @Column(name = "npwp_address")
    private String npwpAddress;

    @Column(name = "cost_center")
    private String costCenter;

    @Column(name = "ksei_safe_code")
    private String kseiSafeCode;

    @Column(name = "selling_agent_code")
    private String sellingAgentCode;

}
