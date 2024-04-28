package com.bayu.billingservice.dto.investmentmanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvestmentManagementRequest {

    private Long id;

    private String code;

    private String name;

    private String email;

    private String address1;

    private String address2;

    private String address3;

    private String address4;

}
