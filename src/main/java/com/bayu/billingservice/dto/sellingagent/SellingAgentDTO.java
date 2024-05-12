package com.bayu.billingservice.dto.sellingagent;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellingAgentDTO extends ApprovalDTO {

    private Long dataChangeId;

    private Long id;

    private String code;

    private String name;

    private String gl;

    private String glName;

    private String account;

    private String accountName;

    private String email;

    private String address;

    private String description;
}
