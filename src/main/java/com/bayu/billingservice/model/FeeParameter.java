package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.Approval;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "fee_parameter")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FeeParameter extends Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fee_code")
    private String feeCode;

    @Column(name = "fee_name")
    private String feeName;

    @Column(name = "fee_description")
    private String feeDescription;

    @Column(name = "fee_value")
    private BigDecimal feeValue;

}
