package com.bayu.billingservice.dto.feeschedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeScheduleApproveRequest {

    private String approverId;

    private String approverIPAddress;

    private String dataChangeId;

    private FeeScheduleDTO data;
}
