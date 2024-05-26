package com.bayu.billingservice.dto.exchangerate;

import com.bayu.billingservice.dto.ApprovalIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateApproveRequest extends ApprovalIdentifierRequest {

    private String dataChangeId;

}
