package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "billing_number")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sequence_number")
    private int sequenceNumber;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private int year;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "number")
    private String number;  // Column to store the original billing number
}
