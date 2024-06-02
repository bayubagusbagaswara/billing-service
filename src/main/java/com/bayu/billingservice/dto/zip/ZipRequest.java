package com.bayu.billingservice.dto.zip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZipRequest {

    private String category;

    private String monthYear;

    private String investmentManagementName;
}
