package com.bayu.billingservice.dto.sellingagent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSellingAgentRequest {

    private String inputerId;
    private String inputerIPAddress;

    private Long id;

    private String code;

    private String name;
}
