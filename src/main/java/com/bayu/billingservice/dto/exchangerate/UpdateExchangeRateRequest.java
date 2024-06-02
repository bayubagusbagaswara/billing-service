package com.bayu.billingservice.dto.exchangerate;

import com.bayu.billingservice.dto.approval.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExchangeRateRequest extends InputIdentifierRequest {

    private Long id;

    private LocalDate date;

    private String currency;

    private String value;
}
