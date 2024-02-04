package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ksei_safekeeping_fee")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KSEISafekeepingFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "fee_description")
    private String feeDescription;

    @Column(name = "aid_4_digit")
    private String aid4Digit;

    @Column(name = "amount_fee")
    private BigDecimal amountFee;
}
