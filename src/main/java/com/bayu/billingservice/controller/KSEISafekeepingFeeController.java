package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.model.KSEISafekeepingFee;
import com.bayu.billingservice.service.KSEISafekeepingFeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/ksei-safe")
public class KSEISafekeepingFeeController {

    @Value("${file.path.ksei-safe}")
    private String filePath;

    private final KSEISafekeepingFeeService kseiSafekeepingFeeService;

    public KSEISafekeepingFeeController(KSEISafekeepingFeeService kseiSafekeepingFeeService) {
        this.kseiSafekeepingFeeService = kseiSafekeepingFeeService;
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
    public ResponseEntity<ResponseDTO<List<KSEISafekeepingFee>>> getAll() {
        List<KSEISafekeepingFee> kseiSafekeepingFeeList = kseiSafekeepingFeeService.getAll();

        ResponseDTO<List<KSEISafekeepingFee>> response = ResponseDTO.<List<KSEISafekeepingFee>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(kseiSafekeepingFeeList)
                .build();

        return ResponseEntity.ok().body(response);
    }
}
