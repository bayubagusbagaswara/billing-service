package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.core.Core1DTO;
import com.bayu.billingservice.service.Core1CalculateService;
import com.bayu.billingservice.service.Core1GeneratePDFService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/core-1")
@RequiredArgsConstructor
public class Core1Controller {

    private final Core1CalculateService calculateService;
    private final Core1GeneratePDFService generatePDFService;

    @PostMapping(path = "/calculate")
    public ResponseEntity<ResponseDTO<String>> calculate(@RequestBody CoreCalculateRequest request) {
        String status = calculateService.calculate(request);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "/generate-pdf")
    public ResponseEntity<ResponseDTO<String>> generatePdf(@RequestBody CoreCalculateRequest request) {
        String status = generatePDFService.generatePDF(request);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<Core1DTO>>> getAll() {
        List<Core1DTO> core1DTOList = generatePDFService.getAll();

        ResponseDTO<List<Core1DTO>> response = ResponseDTO.<List<Core1DTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(core1DTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
