package com.bayu.billingservice.dto.investmentmanagement;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestmentManagementDTO extends ApprovalDTO {

    private Long id;

//    @JsonProperty(value = "MI Code")
    @NotBlank(message = "Code cannot be blank")
    private String code;

//    @JsonProperty(value = "MI Name")
    @NotBlank(message = "Name cannot be blank")
    private String name;

//    @JsonProperty(value = "MI Email")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is not valid")
    private String email;

//    @JsonProperty(value = "Address 1")
    private String address1;

//    @JsonProperty(value = "Address 2")
    private String address2;

//    @JsonProperty(value = "Address 3")
    private String address3;

//    @JsonProperty(value = "Address 4")
    private String address4;
}
