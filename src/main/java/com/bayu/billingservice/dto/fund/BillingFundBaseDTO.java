package com.bayu.billingservice.dto.fund;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public abstract class BillingFundBaseDTO extends ApprovalDTO {

    private Instant createdAt;
    private Instant updatedAt;

    private String approvalStatus;
    private String billingStatus;

    private String customerCode;
    private String customerName;

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
    private String investmentManagementAddress1;
    private String investmentManagementAddress2;
    private String investmentManagementAddress3;
    private String investmentManagementAddress4;
    private String investmentManagementEmail;
    private String investmentManagementUniqueKey;

    private String account;
    private String accountName;
    private String currency;

}
