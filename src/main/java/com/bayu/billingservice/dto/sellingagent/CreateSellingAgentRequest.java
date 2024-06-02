package com.bayu.billingservice.dto.sellingagent;

import com.bayu.billingservice.dto.approval.InputIdentifierRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSellingAgentRequest extends InputIdentifierRequest {

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Code must contain only alphanumeric characters")
    @NotBlank(message = "Code cannot be empty")
    private String code;

    @NotBlank(message = "Name cannot be empty")
    private String name;
}
