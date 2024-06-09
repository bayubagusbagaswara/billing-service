package com.bayu.billingservice.dto.reportgenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportGeneratorRequest {

    private Instant dateNow;
    private String investmentManagementCode;
    private String investmentManagementName;
    private String investmentManagementEmail;
    private String investmentManagementUniqueKey;
    private String customerCode;
    private String customerName;
    private String billingCategory;
    private String billingType;
    private String billingPeriod;
    private String month;
    private Integer year;
    private String currency;
    private String fileName;
    private String filePath;
    private String status;
    private String description;

}
