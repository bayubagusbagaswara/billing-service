package com.bayu.billingservice.dto.assettransfercustomer;

import com.bayu.billingservice.dto.ApprovalIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AssetTransferCustomerApproveRequest extends ApprovalIdentifierRequest {

    private String dataChangeId;

}
