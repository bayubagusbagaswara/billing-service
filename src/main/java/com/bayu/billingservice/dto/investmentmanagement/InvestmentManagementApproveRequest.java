package com.bayu.billingservice.dto.investmentmanagement;

import com.bayu.billingservice.dto.ApprovalIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentManagementApproveRequest extends ApprovalIdentifierRequest {

    private String dataChangeId;

    // private InvestmentManagementDTO data;
}
