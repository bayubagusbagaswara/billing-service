package com.bayu.billingservice.dto.feeparameter;

import com.bayu.billingservice.dto.approval.InputIdentifierRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeeParameterListRequest extends InputIdentifierRequest {

    private List<UpdateFeeParameterDataListRequest> updateFeeParameterDataListRequests;

}
