package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeparameter.*;
import com.bayu.billingservice.service.FeeParameterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
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
    private static final String MENU_FEE_PARAMETER = "Fee Parameter";

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<FeeParameterResponse>> createSingleData(@RequestBody CreateFeeParameterRequest createFeeParameterRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/fee-parameter/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_PARAMETER)
                .build();
        FeeParameterResponse list = feeParameterService.createSingleData(createFeeParameterRequest, dataChangeDTO);
        ResponseDTO<FeeParameterResponse> response = ResponseDTO.<FeeParameterResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(list)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "/create-list")
    public ResponseEntity<ResponseDTO<FeeParameterResponse>> createList(@RequestBody FeeParameterListRequest createFeeParameterListRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/fee-parameter/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_PARAMETER)
                .build();

        FeeParameterResponse list = feeParameterService.createMultipleData(createFeeParameterListRequest, dataChangeDTO);
        ResponseDTO<FeeParameterResponse> response = ResponseDTO.<FeeParameterResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(list)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create/approve")
    public ResponseEntity<ResponseDTO<FeeParameterResponse>> createSingleApprove(@RequestBody FeeParameterApproveRequest createFeeParameterListRequest) {
        FeeParameterResponse listApprove = feeParameterService.createSingleApprove(createFeeParameterListRequest);
        ResponseDTO<FeeParameterResponse> response = ResponseDTO.<FeeParameterResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update-list")
    public ResponseEntity<ResponseDTO<FeeParameterResponse>> updateMultipleData(@RequestBody FeeParameterListRequest updateFeeParameterListRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/fee-parameter/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_PARAMETER)
                .build();
        FeeParameterResponse list = feeParameterService.updateMultipleData(updateFeeParameterListRequest, dataChangeDTO);
        ResponseDTO<FeeParameterResponse> response = ResponseDTO.<FeeParameterResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(list)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update/approve")
    public ResponseEntity<ResponseDTO<FeeParameterResponse>> updateSingleApprove(@RequestBody FeeParameterApproveRequest updateFeeParameterListRequest) {
        FeeParameterResponse listApprove = feeParameterService.updateSingleApprove(updateFeeParameterListRequest);
        ResponseDTO<FeeParameterResponse> response = ResponseDTO.<FeeParameterResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
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
