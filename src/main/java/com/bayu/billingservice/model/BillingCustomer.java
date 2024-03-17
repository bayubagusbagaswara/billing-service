package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "billing_customer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code")
    private String customerCode; // aid

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_minimum_fee")
    private BigDecimal customerMinimumFee; // 5.000.000 etc

    @Column(name = "customer_safekeeping_fee")
    private BigDecimal customerSafekeepingFee; // 0.5 etc

    // MI information
    @Column(name = "mi_name")
    private String investmentManagementName;

    @Column(name = "mi_address")
    private String investmentManagementAddress;

    // Account Transaction information
    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber; //  GL 812017 tapi perlu dicek apakah accountNumber ini ada isinya atau tidak

    @Column(name = "cost_center")
    private String costCenter; // CC 943371

    @Column(name = "account_bank")
    private String accountBank;

    @Column(name = "debit_transfer")
    private BigDecimal debitTransfer;

    @Column(name = "gl_account_result")
    private String glAccountResult;

    // NPWP information
    @Column(name = "npwp_number")
    private String npwpNumber;

    @Column(name = "npwp_name")
    private String npwpName;

    @Column(name = "npwp_address")
    private String npwpAddress;

    // KSEI Safekeeping Fee information
    @Column(name = "ksei_safe_code")
    private String kseiSafeCode;

    // Selling Agent information
    @Column(name = "selling_agent")
    private String sellingAgent;

    // Other information
    @Column(name = "currency")
    private String currency;

    // Billing Information
    @Column(name = "billing_category")
    private String billingCategory;

    @Column(name = "billing_type")
    private String billingType;

    @Column(name = "billing_template")
    private String billingTemplate;

}
