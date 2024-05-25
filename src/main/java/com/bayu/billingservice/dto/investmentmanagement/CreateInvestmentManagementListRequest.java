package com.bayu.billingservice.dto.investmentmanagement;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvestmentManagementListRequest extends InputIdentifierRequest {

    private List<CreateInvestmentManagementDataListRequest> createInvestmentManagementDataListRequests;

}
