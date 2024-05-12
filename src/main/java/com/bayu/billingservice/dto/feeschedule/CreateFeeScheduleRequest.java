package com.bayu.billingservice.dto.feeschedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeeScheduleRequest {

    private String inputId;

    private String inputIPAddress;

    private String feeMinimum;

    private String feeMaximum;

    private String feeAmount;
}
