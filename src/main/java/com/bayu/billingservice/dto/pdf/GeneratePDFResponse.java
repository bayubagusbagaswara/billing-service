package com.bayu.billingservice.dto.pdf;

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
public class GeneratePDFResponse {

    private Integer totalDataSuccess;

    private Integer totalDataFailed;

    private List<ErrorMessageDTO> errorMessageDTOList;
}
