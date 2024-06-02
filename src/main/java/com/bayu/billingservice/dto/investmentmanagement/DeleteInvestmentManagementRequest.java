package com.bayu.billingservice.dto.investmentmanagement;

import com.bayu.billingservice.dto.approval.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteInvestmentManagementRequest extends InputIdentifierRequest {

    private Long id;

}
