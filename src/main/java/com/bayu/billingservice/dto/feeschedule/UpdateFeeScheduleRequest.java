package com.bayu.billingservice.dto.feeschedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeeScheduleRequest {

    private String inputerId;
    private String inputerIPAddress;

    private Long id;

    @NotBlank(message = "Fee Minimum cannot be empty")
    private String feeMinimum;

    @NotBlank(message = "Fee Maximum cannot be empty")
    private String feeMaximum;

    @NotBlank(message = "Fee Amount cannot be empty")
    private String feeAmount;
}
