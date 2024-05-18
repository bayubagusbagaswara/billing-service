package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.BaseAudit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sk_trans")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SkTransaction extends BaseAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_id")
    private String tradeId;

    @Column(name = "portfolio_code")
    private String portfolioCode;

    @Column(name = "security_type")
    private String securityType;

    @Column(name = "security_short_name")
    private String securityShortName;

    @Column(name = "security_name")
    private String securityName;

    @Column(name = "type")
    private String type;

    @Column(name = "trade_date")
    private LocalDate tradeDate;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "delete_status")
    private String deleteStatus;

    @Column(name = "settlement_system")
    private String settlementSystem;

    @Column(name = "sid")
    private String sid;

    @Column(name = "remark")
    private String remark;
}
