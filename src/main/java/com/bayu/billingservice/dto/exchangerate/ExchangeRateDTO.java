package com.bayu.billingservice.dto.exchangerate;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRateDTO extends ApprovalDTO {

    private Long id;

    @NotBlank(message = "Date cannot be blank")
    private String date;

    @NotBlank(message = "Currency cannot be blank")
    private String currency;

    @NotBlank(message = "Value cannot be blank")
    private String value;
}
