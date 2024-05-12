package com.bayu.billingservice.dto.assettransfercustomer;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAssetTransferCustomerDTO {

    @NotNull(message = "Data Change Id cannot be empty")
    private Long dataChangeId;

    @NotNull(message = "Entity Id cannot be empty")
    private Long id;
}
