package com.bayu.billingservice.dto.feeparameter;

import com.bayu.billingservice.dto.approval.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeeParameterRequest extends InputIdentifierRequest {

    private Long id;

    /* fee code cannot be updated */
    private String feeCode;

    /* fee name cannot be updated */
    private String feeName;

    private String feeDescription;

    private String feeValue;

}
