package com.bayu.billingservice.dto.feeparameter;

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
public class CreateFeeParameterListRequest {

    private String inputId;

    private String inputIPAddress;

    private String approveId;

    private String approveIPAddress;

    private List<FeeParameterDTO> feeParameterDTOList;

}
