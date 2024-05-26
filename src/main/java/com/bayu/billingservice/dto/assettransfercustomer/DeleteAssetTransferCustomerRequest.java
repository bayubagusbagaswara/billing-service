package com.bayu.billingservice.dto.assettransfercustomer;

import com.bayu.billingservice.dto.InputIdentifierRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAssetTransferCustomerRequest extends InputIdentifierRequest {

    private Long id;

}
