package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.fund.BillingFundDTO;
import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.fund.FeeReportRequest;
import com.bayu.billingservice.service.FundGeneratePDFService;
import com.bayu.billingservice.service.FundCalculateService;
import com.bayu.billingservice.util.ConvertDateUtil;
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

    @PostMapping(path = "/calculate")
    public ResponseEntity<ResponseDTO<String>> calculate(@RequestBody List<FeeReportRequest> reportRequests,
                                                                      @RequestParam("monthYear") String monthYear) {

        // format month year MMMM-yyyy
        String[] monthFormat = ConvertDateUtil.convertToYearMonthFormat(monthYear);
        String month = monthFormat[0];
        int year = Integer.parseInt(monthFormat[1]);

        String status = fundCalculateService.calculate(reportRequests, month, year);

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

        String[] monthFormat = ConvertDateUtil.convertToYearMonthFormat(monthYear);
        String month = monthFormat[0];
        int year = Integer.parseInt(monthFormat[1]);

        String statusGenerate = fundGeneratePDFService.generatePDF(category, month, year);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(statusGenerate)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingFundDTO>>> getAll() {
        List<BillingFundDTO> billingFundDTOList = fundGeneratePDFService.getAll();

        ResponseDTO<List<BillingFundDTO>> response = ResponseDTO.<List<BillingFundDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(billingFundDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

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

}
