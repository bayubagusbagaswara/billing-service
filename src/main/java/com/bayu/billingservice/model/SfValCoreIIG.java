package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "sf_val_core_iig")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfValCoreIIG {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code_group", columnDefinition = "varchar(255) default 'IIG'")
    private String customerCodeGroup;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "date")
    private Integer date; // 1-31

    @Column(name = "total_holding")
    private BigDecimal totalHolding;

    @Column(name = "price_trub")
    private Integer priceTRUB;

    @Column(name = "total_market_value")
    private BigDecimal totalMarketValue;

    @Column(name = "safekeeping_fee")
    private BigDecimal safekeepingFee;

}
