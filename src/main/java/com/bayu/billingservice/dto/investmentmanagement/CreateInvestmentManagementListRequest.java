package com.bayu.billingservice.dto.investmentmanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvestmentManagementListRequest {

    private Long dataChangeId; // use when approve

    private String approvalStatus;

    private String inputId;

    private String inputIPAddress;

    private String approveId; // use when approve

    private String approveIPAddress; // use when approve

    private List<InvestmentManagementDTO> investmentManagementRequestList;

}
