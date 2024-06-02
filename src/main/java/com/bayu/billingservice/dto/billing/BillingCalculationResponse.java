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
public class BillingCalculationResponse {

    private Integer totalDataSuccess;

    private Integer totalDataFailed;

    private List<BillingCalculationErrorMessageDTO> errorMessages;
}
