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
    private BillingStatus billingStatus;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "bill_category")
    private String billingCategory;

    @Column(name = "bill_type")
    private String billingType;

    @Column(name = "bill_template")
    private String billingTemplate;


    // Data in PDF Template
    @Column(name = "bill_number")
    private String billingNumber;

    @Column(name = "bill_period")
    private String billingPeriod; // November 2023

    @Column(name = "bill_statement_date")
    private String billingStatementDate; // tanggal billing di generate (5-Des-2023)

    @Column(name = "bill_payment_due_date")
    private String billingPaymentDueDate; // tanggal billing di generate ditambah 14 hari


    // Data Investment Management in PDF Template
    @Column(name = "mi_code")
    private String investmentManagementCode;

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

    // Data Account Customer in PDF Template
    @Column(name = "account_name")
    private String accountName; // this is same with GL Name

    @Column(name = "account_number")
    private String accountNumber; // this is same with GL Number (account)

    @Column(name = "account_bank")
    private String accountBank; // this is bank name

    @Column(name = "currency")
    private String currency;

    private String accountCredit; // yg atas

    private String accountDebit; // yg dibawah (kotak billing)

}
