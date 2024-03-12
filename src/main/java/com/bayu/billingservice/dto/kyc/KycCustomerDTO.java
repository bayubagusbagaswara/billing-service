package com.bayu.billingservice.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycCustomerDTO {

    private Long id;

    private String aid;

    private String kseiSafeCode;

    private BigDecimal minimumFee;

    private BigDecimal customerFee;

    private String journal;

    private String billingCategory;

    private String billingType;

    private String billingTemplate;
}
