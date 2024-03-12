package com.bayu.billingservice.dto.fund;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
public abstract class BillingFundBaseDTO {

    private Instant createdAt;
    private Instant updatedAt;

    private String approvalStatus;
    private String aid;
    private String month;
    private String year;

    private String billingNumber;
    private String billingPeriod;
    private String billingStatementDate;
    private String billingPaymentDueDate;
    private String billingCategory;
    private String billingType;
    private String billingTemplate;

    private String investmentManagementName;
    private String investmentManagementAddress;

    private String productName; // or security name

    private String accountName;
    private String accountNumber;
    private String accountBank;

}
