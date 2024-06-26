package com.bayu.billingservice.dto.investmentmanagement;

import com.bayu.billingservice.dto.approval.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvestmentManagementListRequest extends InputIdentifierRequest {

    private List<UpdateInvestmentManagementDataListRequest> updateInvestmentManagementDataListRequests;

}
