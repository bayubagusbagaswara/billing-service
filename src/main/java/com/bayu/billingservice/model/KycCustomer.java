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

    @Column(name = "ksei_safe_code")
    private String kseiSafeCode;

    @Column(name = "minimum_fee")
    private BigDecimal minimumFee;

    @Column(name = "customer_fee")
    private double customerFee;

    @Column(name = "journal")
    private String journal;

    @Column(name = "billing_category")
    private String billingCategory;

    @Column(name = "billing_type")
    private String billingType;

    @Column(name = "billing_template")
    private String billingTemplate;

}
