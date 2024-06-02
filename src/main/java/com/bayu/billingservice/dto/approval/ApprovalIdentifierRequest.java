package com.bayu.billingservice.dto.approval;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class ApprovalIdentifierRequest {

    private String approveId;

}
