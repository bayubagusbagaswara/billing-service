package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * relation for Billing Type and Billing Template
 */
@Entity
@Table(name = "billing_type_template")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingTypeTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "billing_type")
    private String billingType;

    @Column(name = "billing_template")
    private String billingTemplate;

}
