package com.bayu.billingservice.dto.feeschedule;

import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeeScheduleListRequest {

    private String inputId;

    private String inputIPAddress;

    private String approveId;

    private String approveIPAddress;

    private List<FeeScheduleDTO> feeScheduleDTOList;
}
