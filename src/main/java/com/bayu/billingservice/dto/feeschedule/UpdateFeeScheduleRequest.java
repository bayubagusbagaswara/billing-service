package com.bayu.billingservice.dto.feeschedule;

import com.bayu.billingservice.dto.approval.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * jangan taruh validation di update request, karena bisa null atau string kosong
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeeScheduleRequest extends InputIdentifierRequest {

    private Long id;

    private String feeMinimum;

    private String feeMaximum;

    private String feeAmount;
}
