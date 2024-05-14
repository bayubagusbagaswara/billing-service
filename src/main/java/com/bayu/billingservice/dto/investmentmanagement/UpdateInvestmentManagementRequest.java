package com.bayu.billingservice.dto.investmentmanagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvestmentManagementRequest {

    private String inputId;
    private String inputIPAddress;

    private Long id;

    @JsonProperty(value = "MI Code")
    @NotBlank(message = "Code cannot be empty")
    private String code;

    @JsonProperty(value = "MI Name")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @JsonProperty(value = "MI Email")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is not valid")
    private String email;

    @JsonProperty(value = "Alamat 1")
    private String address1;

    @JsonProperty(value = "Alamat 2")
    private String address2;

    @JsonProperty(value = "Alamat 3")
    private String address3;

    @JsonProperty(value = "Alamat 4")
    private String address4;
}
