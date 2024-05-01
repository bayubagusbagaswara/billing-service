package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.BillingCustomerDTO;
import com.bayu.billingservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/kyc-customer")
@RequiredArgsConstructor
public class BillingCustomerController {

    private final CustomerService customerService;

    @GetMapping(path = "/create")
    public ResponseEntity<ResponseDTO<BillingCustomerDTO>> create(@RequestBody CreateKycRequest request) {
        log.info("Start Create Mock Kyc Customer");

        BillingCustomerDTO billingCustomerDTO = customerService.create(request);

        ResponseDTO<BillingCustomerDTO> response = ResponseDTO.<BillingCustomerDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .payload(billingCustomerDTO)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingCustomerDTO>>> getAll() {

        List<BillingCustomerDTO> billingCustomerDTOList = customerService.getAll();

        ResponseDTO<List<BillingCustomerDTO>> response = ResponseDTO.<List<BillingCustomerDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(billingCustomerDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> delete() {
        String status = customerService.deleteAll();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
