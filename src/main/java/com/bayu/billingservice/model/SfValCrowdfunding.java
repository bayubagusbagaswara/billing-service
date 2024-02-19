package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sf_val_crowdfunding")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SfValCrowdfunding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
