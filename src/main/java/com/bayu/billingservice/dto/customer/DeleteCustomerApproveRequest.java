package com.bayu.billingservice.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCustomerApproveRequest {

    private String approveId;
    private String approveIPAddress;

    private String dataChangeId;

    private CustomerDTO data;
}
