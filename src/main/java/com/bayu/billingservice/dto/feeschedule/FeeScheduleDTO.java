package com.bayu.billingservice.dto.feeschedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.services.billingservice.dto.approval.ApprovalDTO;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeeScheduleDTO extends ApprovalDTO {

    private Long dataChangeId;

    private Long id;

    private String feeMinimum;

    private String feeMaximum;

    private String feeAmount;
}
