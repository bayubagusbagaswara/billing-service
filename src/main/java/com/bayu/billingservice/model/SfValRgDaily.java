package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.BaseAudit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sf_val_rg_daily")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SfValRgDaily extends BaseAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch")
    private Integer batch;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private String month; // February

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
