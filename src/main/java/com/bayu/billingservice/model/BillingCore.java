package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "billing_cores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
