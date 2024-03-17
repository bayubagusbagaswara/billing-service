package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * berisi tentang nama file hasil generate pdf billing
 * beserat billing category, billing type, billing template, aid,
 */
@Entity
@Table(name = "billing_file_name")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingFileName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aid")
    private String aid;

    @Column(name = "billing_category")
    private String billingCategory;

    @Column(name = "billing_type")
    private String billingType;

    @Column(name = "billing_template")
    private String billingTemplate;

    @Column(name = "file_name")
    private String fileName;

}
