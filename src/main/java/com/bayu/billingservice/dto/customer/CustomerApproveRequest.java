package com.bayu.billingservice.dto.customer;

import com.bayu.billingservice.dto.ApprovalIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerApproveRequest extends ApprovalIdentifierRequest {

    private String dataChangeId;

}
