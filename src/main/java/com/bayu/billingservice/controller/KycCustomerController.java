package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.kyc.CreateKycRequest;
import com.bayu.billingservice.dto.kyc.KycCustomerDTO;
import com.bayu.billingservice.service.KycCustomerService;
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
public class KycCustomerController {

    private final KycCustomerService kycCustomerService;

    @GetMapping(path = "/create")
    public ResponseEntity<ResponseDTO<KycCustomerDTO>> create(@RequestBody CreateKycRequest request) {
        log.info("Start Create Mock Kyc Customer");

        KycCustomerDTO kycCustomerDTO = kycCustomerService.create(request);

        ResponseDTO<KycCustomerDTO> response = ResponseDTO.<KycCustomerDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .payload(kycCustomerDTO)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<KycCustomerDTO>>> getAll() {

        List<KycCustomerDTO> kycCustomerDTOList = kycCustomerService.getAll();

        ResponseDTO<List<KycCustomerDTO>> response = ResponseDTO.<List<KycCustomerDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(kycCustomerDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> delete() {
        String status = kycCustomerService.deleteAll();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
