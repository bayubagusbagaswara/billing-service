package com.bayu.billingservice.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateKycRequest {

    private String aid;

    private String kseiSafeCode;

    private String minimumFee;

    private String customerSafekeepingFee;

    private String journal;

    private String billingCategory;

    private String billingType;

}
