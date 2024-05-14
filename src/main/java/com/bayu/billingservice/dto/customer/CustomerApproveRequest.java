package com.bayu.billingservice.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerApproveRequest {

    private String approverId;
    private String approverIPAddress;

    private String dataChangeId;

    private CustomerDTO data;

}
