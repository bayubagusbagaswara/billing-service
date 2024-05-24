package com.bayu.billingservice.dto.investmentmanagement;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvestmentManagementListRequest extends InputIdentifierRequest {

    private List<UpdateInvestmentManagementDataListRequest> updateInvestmentManagementDataListRequests;

}
