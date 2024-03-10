package com.bayu.billingservice.dto.fund;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class BillingFundBaseDTO {

    private String billingNumber;
}
