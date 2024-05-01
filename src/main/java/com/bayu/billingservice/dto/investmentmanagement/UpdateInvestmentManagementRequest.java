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
public class UpdateInvestmentManagementRequest {

    private Long dataChangeId;
    private String inputId;
    private String inputIPAddress;

    private Long id;

//    @JsonProperty(value = "MI Code")
    private String code;

//    @JsonProperty(value = "MI Name")
    private String name;

    private String email;

    private String address1;

    private String address2;

    private String address3;

    private String address4;

}
