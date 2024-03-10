package com.bayu.billingservice.model.base;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public abstract class BaseBilling extends BaseAudit {

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "bill_number")
    private String billingNumber;

    @Column(name = "bill_period")
    private String billingPeriod; // November 2023

    @Column(name = "bill_statement_date")
    private String billingStatementDate; // tanggal billing di generate (5-Des-2023)

    @Column(name = "bill_payment_due_date")
    private String billingPaymentDueDate; // tanggal billing di generate ditambah 14 hari

    @Column(name = "bill_category")
    private String billingCategory;

    @Column(name = "bill_type")
    private String billingType;

    @Column(name = "bill_template")
    private String billingTemplate;

    @Column(name = "mi_name")
    private String investmentManagementName;

    @Column(name = "min_address")
    private String investmentManagementAddress;

    @Column(name = "product_name")
    private String productName; // or security name

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

}
