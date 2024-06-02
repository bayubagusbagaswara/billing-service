package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.BillingCalculationResponse;
import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.service.CoreType1Service;
import jakarta.persistence.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/billing/core")
@RequiredArgsConstructor
@Slf4j
public class CoreController {

    private final CoreType1Service coreType1Service;

    @PostMapping(path = "/type-1")
    public ResponseEntity<ResponseDTO<BillingCalculationResponse>> coreType1(@RequestBody CoreCalculateRequest request) {
        BillingCalculationResponse calculate = coreType1Service.calculate(request);
        ResponseDTO<BillingCalculationResponse> response = ResponseDTO.<BillingCalculationResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(calculate)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/type-1/all")
    public ResponseEntity<ResponseDTO<List<BillingCore>>> getAllType1() {
        List<BillingCore> all = coreType1Service.getAll();
        ResponseDTO<List<BillingCore>> response = ResponseDTO.<List<BillingCore>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(all)
                .build();
        return ResponseEntity.ok(response);
    }
}
