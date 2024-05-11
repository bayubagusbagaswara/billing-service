package com.bayu.billingservice.dto.sellingagent;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SellingAgentDTO extends ApprovalDTO {

    private Long id;

    private String code;

    private String name;
}
