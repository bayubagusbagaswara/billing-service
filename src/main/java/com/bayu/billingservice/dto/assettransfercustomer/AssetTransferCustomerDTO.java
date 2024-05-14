package com.bayu.billingservice.dto.assettransfercustomer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.services.billingservice.dto.approval.ApprovalDTO;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetTransferCustomerDTO extends ApprovalDTO {

    private Long id;

    private String customerCode;

    private String securityCode;

    private String amount;

    private String effectiveDate;

    private String transferAssetType;

    private String isEnable;

}
