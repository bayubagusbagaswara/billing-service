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

    private String inputerId;
    private String inputerIPAddress;

    private List<FeeScheduleDTO> feeScheduleDTOList;
}
