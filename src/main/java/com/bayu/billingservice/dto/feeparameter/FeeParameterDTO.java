package com.bayu.billingservice.dto.feeparameter;

import com.bayu.billingservice.dto.approval.ApprovalDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeeParameterDTO extends ApprovalDTO  {

    private Long dataChangeId;

    private Long id;

    private String code;

    private String name;

    private String description;

    private String value;
}
