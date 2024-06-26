package com.bayu.billingservice.dto.sellingagent;

import com.bayu.billingservice.dto.ErrorMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellingAgentResponse {

    private Integer totalDataSuccess;

    private Integer totalDataFailed;

    private List<ErrorMessageDTO> errorMessageDTOList;
}
