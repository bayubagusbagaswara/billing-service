package com.bayu.billingservice.dto.assettransfercustomer;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetTransferCustomerDTO extends ApprovalDTO {

    private Long dataChangeId;

    private Long id;

    private String customerCode;

    private String securityCode;

    private String amount;

    private String effectiveDate;

    private String transferAssetType;

    private String isEnable;
}
