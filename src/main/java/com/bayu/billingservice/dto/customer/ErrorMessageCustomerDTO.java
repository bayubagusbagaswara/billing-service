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
public class ErrorMessageCustomerDTO {

    private String code;

    private List<String> errorMessages;
}
