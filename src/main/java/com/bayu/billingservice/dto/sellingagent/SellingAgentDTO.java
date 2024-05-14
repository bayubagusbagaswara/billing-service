package com.bayu.billingservice.dto.sellingagent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.services.billingservice.dto.approval.ApprovalDTO;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellingAgentDTO extends ApprovalDTO {

    private Long id;

    private String code;

    private String name;

}
