package com.bayu.billingservice.dto.feeparameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeeParameterRequest {

    private String inputId;
    private String inputIPAddress;

    private Long id;

    private String code;
    private String name;
    private String description;
    private String value;
}
