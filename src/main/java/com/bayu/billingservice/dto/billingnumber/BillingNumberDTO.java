package com.bayu.billingservice.dto.billingnumber;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingNumberDTO {

    private String id;
    private String sequenceNumber;
    private String month;
    private String year;
    private String createdDate;
    private String number;
}
