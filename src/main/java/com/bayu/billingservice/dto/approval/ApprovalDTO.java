package com.bayu.billingservice.dto.approval;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ApprovalDTO {

    private String approvalStatus;
    private String inputId;
    private String inputIPAddress;
    private String inputDate;
    private String approveId;
    private String approveIPAddress;
    private String approveDate;
}
