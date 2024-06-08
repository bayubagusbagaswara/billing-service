package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.dto.fund.FundCalculateRequest;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.service.FundCalculateV2Service;
import com.bayu.billingservice.service.FundGeneralService;
import com.bayu.billingservice.service.FundGeneratePDFService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/fund")
@RequiredArgsConstructor
public class FundController {

    private final FundGeneratePDFService fundGeneratePDFService;
    private final FundCalculateV2Service fundCalculateV2Service;
    private final FundGeneralService fundGeneralService;

    @PostMapping(path = "/calculate-v2")
    public ResponseEntity<ResponseDTO<BillingCalculationResponse>> calculateV2(@RequestBody List<FeeReportRequest> reportRequests, @RequestParam("monthYear") String monthYear) {
        BillingCalculationResponse calculate = fundCalculateV2Service.calculate(reportRequests, monthYear);
        ResponseDTO<BillingCalculationResponse> responseDTO = ResponseDTO.<BillingCalculationResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(calculate)
                .build();
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(path = "/generate-pdf")
    public ResponseEntity<ResponseDTO<String>> generatePDF(@RequestBody FundCalculateRequest request) {
        String statusGenerate = fundGeneratePDFService.generatePDF(request);
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(statusGenerate)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingFund>>> getAll() {
        List<BillingFund> all = fundGeneralService.getAll();
        ResponseDTO<List<BillingFund>> response = ResponseDTO.<List<BillingFund>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(all)
                .build();
        return ResponseEntity.ok(response);
    }

}
