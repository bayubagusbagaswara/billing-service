package com.bayu.billingservice.dto.assettransfercustomer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssetTransferCustomerDataListRequest {

    @JsonProperty(value = "Customer Code")
    private String customerCode;

    @JsonProperty(value = "Security Code")
    private String securityCode;

    @JsonProperty(value = "Amount")
    private String amount;

    @JsonProperty(value = "Effective Date")
    private String effectiveDate;

    @JsonProperty(value = "Transfer Asset Type")
    private String transferAssetType;

    @JsonProperty(value = "Enable")
    private String enable;

}
