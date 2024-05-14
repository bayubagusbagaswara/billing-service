package com.bayu.billingservice.dto.feeparameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeParameterListRequest {

    private String inputId;

    private String inputIPAddress;

    private List<FeeParameterDTO> feeParameterDTOList;
}
