package com.bayu.billingservice.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycCustomerDTO {

    private Long id;

    private String aid;

    private String kseiSafeCode;

    private double minimumFee;

    private double customerSafekeepingFee;

    private String journal;

    private String billingCategory;

    private String billingType;

}
