package com.bayu.billingservice.model;

import com.bayu.billingservice.model.base.Approval;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "billing_template")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BillingTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "category")
    private String category;

    @Column(name = "type")
    private String type;

    @Column(name = "sub_code")
    private String subCode; // EB or ITAMA

    @Column(name = "currency")
    private String currency;
}
