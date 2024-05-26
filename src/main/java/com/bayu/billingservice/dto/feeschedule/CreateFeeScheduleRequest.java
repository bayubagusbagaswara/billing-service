package com.bayu.billingservice.dto.feeschedule;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeeScheduleRequest extends InputIdentifierRequest {

    @NotBlank(message = "Fee Minimum cannot be blank")
    private String feeMinimum;

    @NotBlank(message = "Fee Maximum cannot be blank")
    private String feeMaximum;

    @NotBlank(message = "Fee Amount cannot be blank")
    private String feeAmount;

}
