package com.bayu.billingservice.dto.investmentmanagement;

import com.bayu.billingservice.dto.ApprovalIdentifierRequest;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentManagementApproveRequest extends ApprovalIdentifierRequest {

    private String dataChangeId;

    private InvestmentManagementDTO data;
}
