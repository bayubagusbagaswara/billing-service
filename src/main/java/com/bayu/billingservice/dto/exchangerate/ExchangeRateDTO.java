package com.bayu.billingservice.dto.exchangerate;

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
public class ExchangeRateDTO extends ApprovalDTO {

    private Long id;

    @NotBlank(message = "Date cannot be empty")
    private String date;

    @NotBlank(message = "Currency cannot be empty")
    private String currency;

    @NotBlank(message = "Value cannot be empty")
    private String value;
}
