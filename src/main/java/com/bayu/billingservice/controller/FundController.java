package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.BillingFundDTO;
import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/fund")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;

    @PostMapping(path = "/generate")
    public ResponseEntity<ResponseDTO<List<BillingFundDTO>>> generate(
            @RequestBody List<FeeReportRequest> reportRequests,
            @RequestParam("date") String date
    ) {
        // date harus Nov 2023
        List<BillingFundDTO> billingFundDTOList = fundService.generateBillingFund(reportRequests,date);
        ResponseDTO<List<BillingFundDTO>> responseDTO = ResponseDTO.<List<BillingFundDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(billingFundDTOList)
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }

}
