package com.bayu.billingservice.dto.exchangerate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExchangeRateRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank(message = "Currency cannot be empty")
    private String currency;

    @Pattern(regexp = "^\\d+(?:\\.\\d+)?$", message = "Value must be in decimal format")
    @NotBlank(message = "Value cannot be empty")
    private String value;

}
