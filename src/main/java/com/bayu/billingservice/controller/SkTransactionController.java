package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.model.SkTransaction;
import com.bayu.billingservice.service.SkTransactionService;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/sk-tran")
@RequiredArgsConstructor
public class SkTransactionController {

    private final SkTransactionService skTransactionService;

    @GetMapping(path = "/read-insert")
    public ResponseEntity<ResponseDTO<String>> readAndInsert() throws IOException, CsvException {
        String filePath = "SKTRAN_1.csv";

        String status = skTransactionService.readFileAndInsertToDB(filePath);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<SkTransaction>>> getAll() {
        List<SkTransaction> skTransactionList = skTransactionService.getAll();

        ResponseDTO<List<SkTransaction>> response = ResponseDTO.<List<SkTransaction>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(skTransactionList)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
