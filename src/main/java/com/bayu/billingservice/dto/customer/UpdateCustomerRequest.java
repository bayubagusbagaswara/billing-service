package com.bayu.billingservice.dto.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateCustomerRequest extends CustomerDTO {

    private Long dataChangeId;
}
