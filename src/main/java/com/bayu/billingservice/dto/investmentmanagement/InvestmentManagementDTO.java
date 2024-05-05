package com.bayu.billingservice.dto.investmentmanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestmentManagementDTO {

    private Long dataChangeId;

    private Long id;

    @NotBlank(message = "Code cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Name must contain only letters and digits")
    private String code;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Address 1 cannot be empty")
    private String address1;

    private String address2;

    private String address3;

    private String address4;

}
