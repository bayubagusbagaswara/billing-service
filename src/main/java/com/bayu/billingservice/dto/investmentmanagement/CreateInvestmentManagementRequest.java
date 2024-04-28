package com.bayu.billingservice.dto.investmentmanagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvestmentManagementRequest {

    @JsonProperty(value = "MI Code")
    private String code;

    @JsonProperty(value = "MI Name")
    private String name;

    @JsonProperty(value = "MI Email")
    private String email;

    @JsonProperty(value = "Address 1")
    private String address1;

    @JsonProperty(value = "Address 2")
    private String address2;

    @JsonProperty(value = "Address 3")
    private String address3;

    @JsonProperty(value = "Address 4")
    private String address4;
}
