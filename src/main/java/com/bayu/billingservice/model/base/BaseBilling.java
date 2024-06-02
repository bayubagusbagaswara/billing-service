package com.bayu.billingservice.model.base;

import com.bayu.billingservice.model.enumerator.BillingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class BaseBilling extends BaseAudit {

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_status")
    private BillingStatus billingStatus;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "bill_number")
    private String billingNumber;

    @Column(name = "bill_period")
    private String billingPeriod;

    @Column(name = "bill_statement_date")
    private String billingStatementDate;

    @Column(name = "bill_payment_due_date")
    private String billingPaymentDueDate;

    @Column(name = "bill_category")
    private String billingCategory;

    @Column(name = "bill_type")
    private String billingType;

    @Column(name = "bill_template")
    private String billingTemplate;

    @Column(name = "mi_name")
    private String investmentManagementName;

    @Column(name = "mi_address_1")
    private String investmentManagementAddress1;

    @Column(name = "mi_address_2")
    private String investmentManagementAddress2;

    @Column(name = "mi_address_3")
    private String investmentManagementAddress3;

    @Column(name = "mi_address_4")
    private String investmentManagementAddress4;

    @Column(name = "mi_email")
    private String investmentManagementEmail;

    @Column(name = "mi_unique_key")
    private String investmentManagementUniqueKey;

    @Column(name = "currency")
    private String currency;

    @Column(name = "account")
    private String account;

    @Column(name = "account_name")
    private String accountName;

    // formatAccountAndCostCenterDebit
    @Column(name = "account_cost_center_debit")
    private String accountCostCenterDebit; // yg atas : payment

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "gefu_created")
    private Boolean gefuCreated;

}
