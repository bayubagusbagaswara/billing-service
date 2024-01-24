package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sktran")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SKTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_id")
    private String tradeId;

    @Column(name = "portfolio_code")
    private String portfolioCode;

    @Column(name = "type_security")
    private String typeSecurity;

    @Column(name = "short_security_name")
    private String shortSecurityName;

    @Column(name = "security_name")
    private String securityName;

    @Column(name = "type")
    private String type;

    @Column(name = "trade_date")
    private LocalDate tradeDate;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "delete_status")
    private String deleteStatus;

    @Column(name = "system")
    private String system;

    @Column(name = "sid")
    private String sid;

    @Column(name = "remark")
    private String remark;
}
