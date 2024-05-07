package com.bayu.billingservice.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * digunakan ketika approve lebih dari 1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCustomerListRequest {

    private String inputId;

    private String inputIPAddress;

    private String approveId;

    private String approveIPAddress;

    private List<DeleteCustomerDTO> investmentManagementDTOList;
}
