package com.bayu.billingservice.dto.assettransfercustomer;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetTransferCustomerDTO extends ApprovalDTO {

    private Long id;

    private String customerCode;

    private String securityCode;

    @Pattern(regexp = "^\\d+(?:\\.\\d+)?$", message = "Amount must be in decimal format")
    private String amount;

    private String effectiveDate;

    private String transferAssetType;

    private String isEnable;

}
