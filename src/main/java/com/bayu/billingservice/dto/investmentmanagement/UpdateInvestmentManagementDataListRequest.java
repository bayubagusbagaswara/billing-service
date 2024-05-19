package com.bayu.billingservice.dto.investmentmanagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * yg mandatory hanya code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvestmentManagementDataListRequest {

    @JsonProperty(value = "MI Code")
    @NotBlank(message = "Code cannot be empty")
    private String code;

    @JsonProperty(value = "MI Name")
    private String name;

    @JsonProperty(value = "MI Email")
    private String email;

    @JsonProperty(value = "MI Unique Key")
    private String uniqueKey;

    @JsonProperty(value = "Address 1")
    private String address1;

    @JsonProperty(value = "Address 2")
    private String address2;

    @JsonProperty(value = "Address 3")
    private String address3;

    @JsonProperty(value = "Address 4")
    private String address4;
}
