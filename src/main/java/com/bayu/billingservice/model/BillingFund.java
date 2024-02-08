package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "billing_funds")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingFund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
