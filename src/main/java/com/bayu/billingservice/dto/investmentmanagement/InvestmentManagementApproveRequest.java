package com.bayu.billingservice.dto.investmentmanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentManagementApproveRequest {

    private String approveId;
    private String approveIPAddress;
    private String dataChangeId;

    private InvestmentManagementDTO data;
}
