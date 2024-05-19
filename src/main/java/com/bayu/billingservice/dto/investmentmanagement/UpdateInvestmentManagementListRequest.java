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
public class UpdateInvestmentManagementListRequest {

    private String inputId;

    private String inputIPAddress;

    private List<UpdateInvestmentManagementDataListRequest> updateInvestmentManagementDataListRequests;
}
