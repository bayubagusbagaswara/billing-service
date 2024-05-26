package com.bayu.billingservice.dto.exchangerate;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExchangeRateRequest extends InputIdentifierRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "Date cannot be empty")
    private LocalDate date;

    @NotBlank(message = "Currency cannot be empty")
    private String currency;

    @Pattern(regexp = "^\\d+(?:\\.\\d+)?$", message = "Rate Value must be in decimal format")
    @NotBlank(message = "Value cannot be empty")
    private String value;

}
