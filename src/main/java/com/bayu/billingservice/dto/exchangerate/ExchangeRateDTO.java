package com.bayu.billingservice.dto.exchangerate;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRateDTO extends ApprovalDTO {

    private Long dataChangeId;

    private Long id;

    private String date;

    private String currency;

    private String value;

}
