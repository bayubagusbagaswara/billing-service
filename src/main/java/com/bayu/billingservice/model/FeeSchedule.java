package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.Approval;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "fee_schedule")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FeeSchedule extends Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fee_minimum")
    private BigDecimal feeMinimum;

    @Column(name = "fee_maximum")
    private BigDecimal feeMaximum;

    @Column(name = "fee_amount")
    private BigDecimal feeAmount;

}
