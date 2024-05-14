package com.bayu.billingservice.dto.feeparameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeParameterApproveRequest {

    private String approverId;
    private String approverIPAddress;

    private String dataChangeId;

    private FeeParameterDTO data;
}
