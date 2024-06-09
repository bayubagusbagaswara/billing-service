package com.bayu.billingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthYearDTO {

    private String monthName;

    private String monthValue; // 1-12

    private Integer year;

}
