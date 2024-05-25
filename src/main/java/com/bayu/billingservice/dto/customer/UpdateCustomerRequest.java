package com.bayu.billingservice.dto.customer;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest extends InputIdentifierRequest {

    private Long id;

    private String customerCode;

    private String subCode;

    private String customerName;

    private String billingCategory;

    private String billingType;

    private String billingTemplate;

    private String currency;

    private String miCode;

    private String miName;

    private String account;

    private String accountName;

    private String debitTransfer;

    private String costCenter;

    private String glAccountHasil;

    private String customerMinimumFee;

    private String customerSafekeepingFee;

    private String customerTransactionHandling;

    private String npwpNumber;

    private String npwpName;

    private String npwpAddress;

    private String kseiSafeCode;

    private String sellingAgent;

    private String gl;

}
