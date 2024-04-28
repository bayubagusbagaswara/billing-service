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
public class CreateInvestmentManagementListResponse {

    private Integer totalDataSuccess;

    private Integer totalDataFailed;

    private List<ErrorMessageInvestmentManagementDTO> errorMessages;
}
