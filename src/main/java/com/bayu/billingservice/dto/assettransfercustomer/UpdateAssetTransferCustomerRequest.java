package com.bayu.billingservice.dto.assettransfercustomer;

import com.bayu.billingservice.dto.approval.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssetTransferCustomerRequest extends InputIdentifierRequest {

    private Long id;

    private String customerCode;

    private String securityCode;

    private String amount;

    private String effectiveDate;

    private String transferAssetType;

    private String enable;

}
