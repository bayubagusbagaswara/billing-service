package com.bayu.billingservice.dto.feeparameter;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeParameterDTO extends ApprovalDTO {

    private Long id;

    @NotBlank(message = "Fee Code cannot be empty")
    private String feeCode;

    @NotBlank(message = "Fee Name cannot be empty")
    private String feeName;

    private String feeDescription;

    @NotBlank(message = "Fee Value cannot be empty")
    private String feeValue;
}
