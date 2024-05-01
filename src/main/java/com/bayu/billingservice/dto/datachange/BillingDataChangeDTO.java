package com.bayu.billingservice.dto.datachange;

import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.model.enumerator.ChangeAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDataChangeDTO {

    private Long id;

    private ApprovalStatus approvalStatus;

    private String inputId;
    private String inputDate;
    private String inputIPAddress;

    private String approveId;
    private String approveDate;
    private String approveIPAddress;

    private ChangeAction changeAction;

    private String entityId;
    private String entityClassName;
    private String tableName;

    private String jsonDataBefore;
    private String jsonDataAfter;

    private String description;

    private String methodHttp;
    private String endpoint;
    private Boolean isRequestBody;
    private Boolean isRequestParam;
    private Boolean isPathVariable;
    private String menu;

}
