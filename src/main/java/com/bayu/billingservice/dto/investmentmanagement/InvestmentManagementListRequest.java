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
public class InvestmentManagementListRequest {

    private String inputId;

    private String inputIPAddress;

    private List<InvestmentManagementDTO> investmentManagementRequestList;

}
