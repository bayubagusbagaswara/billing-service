package com.bayu.billingservice.dto.feeschedule;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFeeScheduleRequest extends InputIdentifierRequest {

    private Long id;

}
