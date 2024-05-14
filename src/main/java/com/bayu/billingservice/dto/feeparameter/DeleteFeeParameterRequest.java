package com.bayu.billingservice.dto.feeparameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFeeParameterRequest {

    private String inputerId;
    private String inputerIPAddress;

    private Long id;
}
