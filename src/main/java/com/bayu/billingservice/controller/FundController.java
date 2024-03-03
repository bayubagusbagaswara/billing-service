package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.service.FundGeneratePDFService;
import com.bayu.billingservice.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/billing-fund")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;
    private final FundGeneratePDFService fundGeneratePDFService;

    @PostMapping(path = "/calculate")
    public ResponseEntity<ResponseDTO<List<BillingFundDTO>>> calculate(@RequestBody List<FeeReportRequest> reportRequests,
                                                                      @RequestParam("monthYear") String monthYear) {
        List<BillingFundDTO> billingFundDTOList = fundService.calculate(reportRequests, monthYear);
        ResponseDTO<List<BillingFundDTO>> responseDTO = ResponseDTO.<List<BillingFundDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(billingFundDTOList)
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }

    // get all billing fund

    // update billing fund (request in List<>)


    @GetMapping(path = "/generate-pdf")
    public ResponseEntity<ResponseDTO<String>> generatePDF() {
        // get all billing filter by status approval is Approved
        String statusGenerate = fundGeneratePDFService.generatePDF();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(statusGenerate)
                .build();
        return ResponseEntity.ok().body(response);
    }

}
