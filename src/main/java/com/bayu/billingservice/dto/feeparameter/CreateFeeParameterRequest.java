package com.bayu.billingservice.dto.feeparameter;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeeParameterRequest extends InputIdentifierRequest {

    @NotBlank(message = "Fee Code cannot be empty")
    private String feeCode;

    @NotBlank(message = "Fee Name cannot be empty")
    private String feeName;

    private String feeDescription;

    @NotBlank(message = "Fee Value cannot be empty")
    private String feeValue;

}
