package com.bayu.billingservice.dto.feeparameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeeParameterDataListRequest {

    @JsonProperty(value = "Fee Code")
    private String feeCode;

    @JsonProperty(value = "Fee Name")
    private String feeName;

    @JsonProperty(value = "Fee Description")
    private String feeDescription;

    @JsonProperty(value = "Fee Value")
    private String feeValue;

}
