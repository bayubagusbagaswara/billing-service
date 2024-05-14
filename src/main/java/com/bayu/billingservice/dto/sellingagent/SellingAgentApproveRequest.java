package com.bayu.billingservice.dto.sellingagent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellingAgentApproveRequest {

    private String approveId;
    private String approveIPAddress;

    private String dataChangeId;

    private SellingAgentDTO data;
}
