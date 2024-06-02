package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.billingnumber.BillingNumberDTO;
import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.service.BillingNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/billing/number")
@RequiredArgsConstructor
public class BillingNumberController {

    private final BillingNumberService billingNumberService;

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingNumberDTO>>> getAll() {
        List<BillingNumberDTO> billingNumberDTOList = billingNumberService.getAll();

        ResponseDTO<List<BillingNumberDTO>> response = ResponseDTO.<List<BillingNumberDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(billingNumberDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
