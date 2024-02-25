package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fee_parameter", uniqueConstraints = {
        @UniqueConstraint(name = "fee_parameter_name_unique", columnNames = "name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "value")
    private double value;

}
