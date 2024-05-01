package com.bayu.billingservice.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerListResponse {

    private Integer totalDataSuccess;
    private Integer totalDataFailed;

    private List<ErrorMessageCustomerDTO> errorMessageCustomerDTOList;



}
