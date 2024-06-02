package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreCalculateRequest {

    private String category; // format : Fund, Core, Retail

    private String type; // format : Type 1, Type 2, Type 3, etc

    private String monthYear; // format : Nov 2023, November 2023
}
