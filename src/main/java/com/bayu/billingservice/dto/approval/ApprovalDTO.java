package com.bayu.billingservice.dto.approval;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class ApprovalDTO {

    private String approvalStatus;
    private String inputId;
    private String inputIPAddress;
    private String inputDate;
    private String approveId;
    private String approveIPAddress;
    private String approveDate;
}
