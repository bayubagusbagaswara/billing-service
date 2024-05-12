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
public class CreateSellingAgentListRequest {

    private String inputId;

    private String inputIPAddress;

    private String approveId;

    private String approveIPAddress;

    private List<SellingAgentDTO> sellingAgentDTOList;
}