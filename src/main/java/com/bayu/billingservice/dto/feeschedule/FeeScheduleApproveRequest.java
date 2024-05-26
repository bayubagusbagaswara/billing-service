package com.bayu.billingservice.dto.feeschedule;

import com.bayu.billingservice.dto.ApprovalIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FeeScheduleApproveRequest extends ApprovalIdentifierRequest {

    private String dataChangeId;

}
