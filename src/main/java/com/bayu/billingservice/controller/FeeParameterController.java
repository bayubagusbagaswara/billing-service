package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.feeparameter.CreateFeeParameterRequest;
import com.bayu.billingservice.dto.feeparameter.FeeParameterDTO;
import com.bayu.billingservice.service.FeeParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
