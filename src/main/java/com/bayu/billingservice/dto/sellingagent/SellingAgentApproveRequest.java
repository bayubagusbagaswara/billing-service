package com.bayu.billingservice.dto.sellingagent;

import com.bayu.billingservice.dto.ApprovalIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SellingAgentApproveRequest extends ApprovalIdentifierRequest {

    private String dataChangeId;

}
