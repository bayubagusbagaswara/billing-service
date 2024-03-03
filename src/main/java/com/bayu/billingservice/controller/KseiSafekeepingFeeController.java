package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.kseisafe.CreateKseiSafeRequest;
import com.bayu.billingservice.model.KseiSafekeepingFee;
import com.bayu.billingservice.service.KseiSafekeepingFeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/ksei-safe")
public class KseiSafekeepingFeeController {

    @Value("${file.path.ksei-safe}")
    private String filePath;

    private final KseiSafekeepingFeeService kseiSafekeepingFeeService;

    public KseiSafekeepingFeeController(KseiSafekeepingFeeService kseiSafekeepingFeeService) {
        this.kseiSafekeepingFeeService = kseiSafekeepingFeeService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<List<KseiSafekeepingFee>>> create(@RequestBody List<CreateKseiSafeRequest> requestList) {
        List<KseiSafekeepingFee> kseiSafekeepingFeeList = kseiSafekeepingFeeService.create(requestList);

        ResponseDTO<List<KseiSafekeepingFee>> response = ResponseDTO.<List<KseiSafekeepingFee>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(kseiSafekeepingFeeList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/read-insert")
    public ResponseEntity<ResponseDTO<String>> readAndInsert() {
        log.info("File Path : {}", filePath);

        String status = kseiSafekeepingFeeService.readAndInsertToDB(filePath);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<KseiSafekeepingFee>>> getAll() {
        List<KseiSafekeepingFee> kseiSafekeepingFeeList = kseiSafekeepingFeeService.getAll();

        ResponseDTO<List<KseiSafekeepingFee>> response = ResponseDTO.<List<KseiSafekeepingFee>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(kseiSafekeepingFeeList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/customer-code")
    public ResponseEntity<ResponseDTO<List<KseiSafekeepingFee>>> getAllByName(@RequestParam("customerCode") String customerCode) {
        List<KseiSafekeepingFee> kseiSafekeepingFeeList = kseiSafekeepingFeeService.getByCustomerCode(customerCode);
        ResponseDTO<List<KseiSafekeepingFee>> response = ResponseDTO.<List<KseiSafekeepingFee>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(kseiSafekeepingFeeList)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/calculate")
    public ResponseEntity<ResponseDTO<BigDecimal>> calculateAmountFeeByCustomerCodeAndMonthAndYear(
            @RequestParam("customerCode") String customerCode,
            @RequestParam("month") String month,
            @RequestParam("year") Integer year) {
        BigDecimal amountFee = kseiSafekeepingFeeService.calculateAmountFeeByCustomerCodeAndMonthAndYear(
                customerCode, month, year);

        ResponseDTO<BigDecimal> response = ResponseDTO.<BigDecimal>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(amountFee)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/calculate/3month")
    public ResponseEntity<ResponseDTO<BigDecimal>> calculateAmountFeeForLast3Months(
            @RequestParam("customerCode") String customerCode,
            @RequestParam("month") String month,
            @RequestParam("year") Integer year) {

        BigDecimal amountFee = kseiSafekeepingFeeService.calculateAmountFeeForLast3Months(customerCode, month, year);

        ResponseDTO<BigDecimal> response = ResponseDTO.<BigDecimal>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(amountFee)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
