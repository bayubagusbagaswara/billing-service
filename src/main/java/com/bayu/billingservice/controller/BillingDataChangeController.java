package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.model.BillingDataChange;
import com.bayu.billingservice.service.BillingDataChangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/data-change")
@RequiredArgsConstructor
@Slf4j
public class BillingDataChangeController {

    private final BillingDataChangeService dataChangeService;

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingDataChange>>> getAll() {
        List<BillingDataChange> all = dataChangeService.getAll();
        ResponseDTO<List<BillingDataChange>> response = ResponseDTO.<List<BillingDataChange>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(all)
                .build();

        return ResponseEntity.ok(response);
    }
}
