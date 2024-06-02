package com.bayu.billingservice.dto.feeparameter;

import com.bayu.billingservice.dto.approval.ApprovalIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FeeParameterApproveRequest extends ApprovalIdentifierRequest {

    private String dataChangeId;

}
