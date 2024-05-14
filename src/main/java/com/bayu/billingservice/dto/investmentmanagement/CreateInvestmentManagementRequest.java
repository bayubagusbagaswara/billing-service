package com.bayu.billingservice.dto.investmentmanagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvestmentManagementRequest {

    private String inputId;

    private String inputIPAddress;

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
