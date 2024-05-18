package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.service.DataChangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/data-change")
@RequiredArgsConstructor
@Slf4j
public class BillingDataChangeController {

    private final DataChangeService dataChangeService;

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingDataChangeDTO>>> getAll() {
        List<BillingDataChangeDTO> all = dataChangeService.getAll();
        ResponseDTO<List<BillingDataChangeDTO>> response = ResponseDTO.<List<BillingDataChangeDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(all)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/all")
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String status = dataChangeService.deleteAll();
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();
        return ResponseEntity.ok(response);
    }
}
