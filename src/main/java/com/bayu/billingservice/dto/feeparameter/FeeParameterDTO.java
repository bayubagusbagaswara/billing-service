package com.bayu.billingservice.dto.feeparameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.services.billingservice.dto.approval.ApprovalDTO;
import lombok.*;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeeParameterDTO extends ApprovalDTO {

    private Long id;

    @NotBlank(message = "Code cannot be empty")
    private String code;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    private String description;

    @NotBlank(message = "Value cannot be empty")
    private String value;
}
