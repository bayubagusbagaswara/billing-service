package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sf_val_rg_monthly")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfValRgMonthly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch")
    private Integer batch;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "aid")
    private String aid; // or portfolio code

    @Column(name = "security_name")
    private String securityName;

    @Column(name = "face_value")
    private BigDecimal faceValue;

    @Column(name = "market_price")
    private String marketPrice;

    @Column(name = "market_value")
    private BigDecimal marketValue; // is faceValue * marketPrice

    @Column(name = "estimation_sk_fee")
    private BigDecimal estimationSafekeepingFee;
}
