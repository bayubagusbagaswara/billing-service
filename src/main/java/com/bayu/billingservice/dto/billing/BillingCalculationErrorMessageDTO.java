package com.bayu.billingservice.dto.billing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCalculationErrorMessageDTO {

    private String customerCode; // aid

    private List<String> errors;
}
