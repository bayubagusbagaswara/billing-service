package com.bayu.billingservice.dto.sellingagent;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSellingAgentRequest extends InputIdentifierRequest {

    private Long id;

    private String code;

    private String name;
}
