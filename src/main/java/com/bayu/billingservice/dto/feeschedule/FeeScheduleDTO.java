package com.bayu.billingservice.dto.feeschedule;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
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
