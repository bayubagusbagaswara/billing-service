package com.bayu.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Core3DTO {

    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;
    private String safekeepingJournal;

}
