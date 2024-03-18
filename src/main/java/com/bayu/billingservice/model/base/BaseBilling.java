package com.bayu.billingservice.model.base;

import jakarta.persistence.Column;
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

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "aid")
    private String aid;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private Integer year;

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

    @Column(name = "mi_address")
    private String investmentManagementAddress;

    @Column(name = "product_name")
    private String productName; // or security name

    @Column(name = "account_name")
    private String accountName; // this is same with GL Name

    @Column(name = "account_number")
    private String accountNumber; // this is same with GL Number (account)

    @Column(name = "cost_center")
    private String costCenter;

    @Column(name = "account_bank")
    private String accountBank; // this is bank name

    @Column(name = "currency")
    private String currency;

}
