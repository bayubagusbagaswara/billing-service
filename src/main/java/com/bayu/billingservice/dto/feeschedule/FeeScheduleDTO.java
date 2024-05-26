package com.bayu.billingservice.dto.feeschedule;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeScheduleDTO extends ApprovalDTO {

    private Long id;

    @Pattern(regexp = "^\\d+(?:\\.\\d+)?$", message = "Fee Minimum must be in decimal format")
    @NotBlank(message = "Fee Minimum cannot be empty")
    private String feeMinimum;

    @Pattern(regexp = "^\\d+(?:\\.\\d+)?$", message = "Fee Maximum must be in decimal format")
    @NotBlank(message = "Fee Maximum cannot be empty")
    private String feeMaximum;

    @Pattern(regexp = "^\\d+(?:\\.\\d+)?$", message = "Fee Amount must be in decimal format")
    @NotBlank(message = "Fee Amount cannot be empty")
    private String feeAmount;
}
