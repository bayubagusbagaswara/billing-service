package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.Approval;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exchange_rate")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate extends Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "currency")
    private String currency;

    @Column(name = "value")
    private BigDecimal value;
}
