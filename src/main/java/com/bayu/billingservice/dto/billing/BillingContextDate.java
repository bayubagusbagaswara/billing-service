package com.bayu.billingservice.dto.billing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingContextDate {

    private Instant dateNow;

    private String monthNameMinus1;

    private Integer yearMinus1;

    private String monthNameNow;

    private Integer yearNow;

}
