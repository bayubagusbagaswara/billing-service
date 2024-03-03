package com.bayu.billingservice.dto.kseisafe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateKseiSafeRequest {

    private String createdDate; // "2023-11-30"

    private String feeDescription; // "Safekeeping fee for account BDMN2OBAL00119"

    private String customerCode; // "BDMN2OBAL00119"

    private String amountFee; // 1466712.34

}
