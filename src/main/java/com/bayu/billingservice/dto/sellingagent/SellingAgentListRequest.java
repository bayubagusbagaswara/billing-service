package com.bayu.billingservice.dto.sellingagent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellingAgentListRequest {

    private String inputerId;
    private String inputerIPAddress;

    private List<SellingAgentDTO> sellingAgentDTOList;
}
