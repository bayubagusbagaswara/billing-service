package com.bayu.billingservice.dto.customer;

import com.bayu.billingservice.dto.investmentmanagement.InvestmentManagementDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerListRequest {

    private String inputId;
    private String inputIPAddress;

    private String approveId;
    private String approveIPAddress;

    private List<InvestmentManagementDTO> investmentManagementDTOList;
}
