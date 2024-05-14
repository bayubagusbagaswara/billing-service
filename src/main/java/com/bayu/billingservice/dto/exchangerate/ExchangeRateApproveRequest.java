package com.bayu.billingservice.dto.exchangerate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateApproveRequest {

    private String approveId;
    private String approveIPAddress;

    private Long dataChangeId;

    private ExchangeRateDTO data;
}
