package com.bayu.billingservice.dto.sellingagent;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellingAgentDTO extends ApprovalDTO {

    private Long id;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Code must contain only alphanumeric characters")
    @NotBlank(message = "Code cannot be empty")
    private String code;

    @NotBlank(message = "Name cannot be empty")
    private String name;

}
