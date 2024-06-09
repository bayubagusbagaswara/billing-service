package com.bayu.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "report_generator")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "investment_management_code")
    private String investmentManagementCode;

    @Column(name = "investment_management_name")
    private String investmentManagementName;

    @Column(name = "investment_management_email")
    private String investmentManagementEmail;

    @Column(name = "investment_management_unique_key")
    private String investmentManagementUniqueKey;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "billing_category")
    private String billingCategory;

    @Column(name = "billing_type")
    private String billingType;

    @Column(name = "currency")
    private String currency;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "billing_period")
    private String billingPeriod;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

}
