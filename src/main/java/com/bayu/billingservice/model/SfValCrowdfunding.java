package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.BaseAudit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sf_val_crowdfunding")
@SuperBuilder
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfValCrowdfunding extends BaseAudit {

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
