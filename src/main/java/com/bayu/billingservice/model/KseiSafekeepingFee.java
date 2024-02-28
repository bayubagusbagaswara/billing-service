package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ksei_safe_fee")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KseiSafekeepingFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "fee_description")
    private String feeDescription;

    @Column(name = "fee_account")
    private String feeAccount;

    @Column(name = "amount_fee")
    private BigDecimal amountFee;
}
