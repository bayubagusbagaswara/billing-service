package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sf_val_crowdfunding")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfValCrowdfunding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Integer number;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "client_code")
    private String clientCode;

    @Column(name = "security_code")
    private String securityCode;

    @Column(name = "face_value")
    private BigDecimal faceValue;

    @Column(name = "market_price")
    private String marketPrice;

    @Column(name = "market_value")
    private BigDecimal marketValue;

    @Column(name = "investor")
    private String investor;

}
