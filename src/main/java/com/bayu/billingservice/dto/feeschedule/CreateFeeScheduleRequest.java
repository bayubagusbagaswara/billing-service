package com.bayu.billingservice.dto.feeschedule;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Fee Minimum cannot be blank")
    private String feeMinimum;

    @NotBlank(message = "Fee Maximum cannot be blank")
    private String feeMaximum;

    @NotBlank(message = "Fee Amount cannot be blank")
    private String feeAmount;

}
