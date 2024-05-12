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
    private String investmentManagementAddressBuilding;
    private String investmentManagementAddressStreet;
    private String investmentManagementAddressCity;
    private String investmentManagementAddressProvince;

    private String productName; // or security name

    private String accountName;
    private String accountNumber;
    private String accountBank;

}
