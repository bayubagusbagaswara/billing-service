package com.bayu.billingservice.dto.iig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSfValCoreIIGRequest {

    private String customerCode;

    private String customerName;

    private String customerFee;

    private String totalHolding;

    private String priceTRUB;
}
