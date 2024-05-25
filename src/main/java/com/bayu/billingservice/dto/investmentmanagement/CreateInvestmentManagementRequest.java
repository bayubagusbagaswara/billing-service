package com.bayu.billingservice.dto.investmentmanagement;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvestmentManagementRequest extends InputIdentifierRequest {

    @NotBlank(message = "Code cannot be empty")
    private String code;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Unique key cannot be empty")
    private String uniqueKey;

    @NotBlank(message = "Address 1 cannot be empty")
    private String address1;

    private String address2;

    private String address3;

    private String address4;
}
