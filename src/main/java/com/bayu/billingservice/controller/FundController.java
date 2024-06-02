package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.billing.BillingCalculationResponse;
import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.model.BillingFund;
import com.bayu.billingservice.service.FundCalculateV2Service;
import com.bayu.billingservice.service.FundGeneralService;
import com.bayu.billingservice.service.FundGeneratePDFService;
import com.bayu.billingservice.service.FundCalculateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/fund")
@RequiredArgsConstructor
public class FundController {

    private final FundCalculateService fundCalculateService;
    private final FundGeneratePDFService fundGeneratePDFService;
    private final FundCalculateV2Service fundCalculateV2Service;
    private final FundGeneralService fundGeneralService;

    @PostMapping(path = "/calculate")
    public ResponseEntity<ResponseDTO<String>> calculate(@RequestBody List<FeeReportRequest> reportRequests,
                                                                      @RequestParam("monthYear") String monthYear) {

        String status = fundCalculateService.calculate(reportRequests, monthYear);

        ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping(path = "/generate-pdf")
    public ResponseEntity<ResponseDTO<String>> generatePDF(@RequestParam("category") String category,
                                                           @RequestParam("monthYear") String monthYear) {


        String statusGenerate = fundGeneratePDFService.generatePDF(category, monthYear);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(statusGenerate)
                .build();

        return ResponseEntity.ok().body(response);
    }

//    @GetMapping(path = "/all")
//    public ResponseEntity<ResponseDTO<List<BillingFundDTO>>> getAll() {
//        List<BillingFundDTO> billingFundDTOList = fundGeneratePDFService.getAll();
//
//        ResponseDTO<List<BillingFundDTO>> response = ResponseDTO.<List<BillingFundDTO>>builder()
//                .code(HttpStatus.OK.value())
//                .message(HttpStatus.OK.getReasonPhrase())
//                .payload(billingFundDTOList)
//                .build();
//
//        return ResponseEntity.ok().body(response);
//    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String status = fundGeneratePDFService.deleteAll();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "/calculate-v2")
    public ResponseEntity<ResponseDTO<BillingCalculationResponse>> calculateV2(@RequestBody List<FeeReportRequest> reportRequests, @RequestParam("monthYear") String monthYear) {

        BillingCalculationResponse calculate = fundCalculateV2Service.calculate(reportRequests, monthYear);

        ResponseDTO<BillingCalculationResponse> responseDTO = ResponseDTO.<BillingCalculationResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(calculate)
                .build();

        return ResponseEntity.ok().body(responseDTO);
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
