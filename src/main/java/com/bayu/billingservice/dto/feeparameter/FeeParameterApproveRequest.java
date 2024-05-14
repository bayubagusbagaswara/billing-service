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

    private String approveId;
    private String approveIPAddress;

    private String dataChangeId;

    private FeeParameterDTO data;
}
