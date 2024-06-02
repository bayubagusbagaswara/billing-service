package com.bayu.billingservice.dto.customer;

import com.bayu.billingservice.dto.approval.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerListRequest extends InputIdentifierRequest {

    private List<CreateCustomerDataListRequest> createCustomerDataListRequests;

}
