package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "kyc_customer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aid")
    private String aid;

    @Column(name = "mi_name")
    private String investmentManagementName;

    @Column(name = "mi_address")
    private String investmentManagementAddress;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_bank")
    private String accountBank;

    @Column(name = "ksei_safe_code")
    private String kseiSafeCode;

    @Column(name = "minimum_fee")
    private BigDecimal minimumFee; // 5.000.000 etc

    @Column(name = "customer_fee")
    private BigDecimal customerFee; // 0.5 etc

    @Column(name = "journal")
    private String journal;

    @Column(name = "billing_category")
    private String billingCategory;

    @Column(name = "billing_type")
    private String billingType;

    @Column(name = "billing_template")
    private String billingTemplate;

}
