package com.bayu.billingservice.dto.exchangerate;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDTO extends ApprovalDTO {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "Date cannot be empty")
    private LocalDate date;

    @NotBlank(message = "Currency cannot be empty")
    private String currency;

    @Pattern(regexp = "^\\d+(?:\\.\\d+)?$", message = "Rate Value must be in decimal format")
    @NotBlank(message = "Value cannot be empty")
    private String value;
}
