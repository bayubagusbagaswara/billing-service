package com.bayu.billingservice.dto.feeparameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeeParameterRequest {

    private String name;

    private String description;

    private String value;
}
