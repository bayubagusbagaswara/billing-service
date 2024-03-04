package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "billing_core_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCoreDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "billing_category")
    private String billingCategory;

    @Column(name = "billing_type")
    private String billingType;

    @Column(name = "portfolio_code")
    private String portfolioCode;

    @Column(name = "security_code")
    private String securityCode;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Column(name = "market_price")
    private BigDecimal marketPrice;

}
