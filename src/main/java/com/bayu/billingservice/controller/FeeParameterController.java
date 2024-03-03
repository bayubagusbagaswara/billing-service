package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.feeparameter.CreateFeeParameterRequest;
import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;
import com.bayu.billingservice.service.FeeParameterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/fee-parameter")
@RequiredArgsConstructor
public class FeeParameterController {

    private final FeeParameterService feeParameterService;

    @GetMapping(path = "/create")
    public ResponseEntity<ResponseDTO<FeeParameterDTO>> create(@RequestBody CreateFeeParameterRequest request) {
        FeeParameterDTO feeParameterDTO = feeParameterService.create(request);

        ResponseDTO<FeeParameterDTO> response = ResponseDTO.<FeeParameterDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .payload(feeParameterDTO)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<FeeParameterDTO>>> getAll() {
        List<FeeParameterDTO> feeParameterDTOList = feeParameterService.getAll();

        ResponseDTO<List<FeeParameterDTO>> response = ResponseDTO.<List<FeeParameterDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(feeParameterDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/name-list")
    public ResponseEntity<ResponseDTO<List<FeeParameterDTO>>> getByNameList(@RequestBody List<String> request) {
        List<FeeParameterDTO> feeParameterDTOList = feeParameterService.getByNameList(request);

        ResponseDTO<List<FeeParameterDTO>> response = ResponseDTO.<List<FeeParameterDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(feeParameterDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/name-list/value")
    public ResponseEntity<ResponseDTO<Map<String, BigDecimal>>> getValueByNameList(@RequestBody List<String> request) {
        Map<String, BigDecimal> feeParameterServiceValueByNameList = feeParameterService.getValueByNameList(request);

        // Iterating over entries
        for (Map.Entry<String, BigDecimal> entry : feeParameterServiceValueByNameList.entrySet()) {
            log.info("Key: {}, Value: {}", entry.getKey(), entry.getValue());
        }

        ResponseDTO<Map<String, BigDecimal>> response = ResponseDTO.<Map<String, BigDecimal>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(feeParameterServiceValueByNameList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> delete() {
        String status = feeParameterService.deleteAll();
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
