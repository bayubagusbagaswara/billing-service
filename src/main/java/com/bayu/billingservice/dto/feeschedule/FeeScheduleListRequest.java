package com.bayu.billingservice.dto.feeschedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeScheduleListRequest {

    private String inputId;
    private String inputIPAddress;

    private List<FeeScheduleDTO> feeScheduleDTOList;
}
