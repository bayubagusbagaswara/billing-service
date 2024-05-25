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
public class UpdateInvestmentManagementRequest extends InputIdentifierRequest {

    private Long id;

    // private String code;

    private String name;

    private String email;

    private String uniqueKey;

    private String address1;

    private String address2;

    private String address3;

    private String address4;
}
