package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.exchangerate.*;
import com.bayu.billingservice.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/exchange-rate")
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateController {

    private static final String MENU_EXCHANGE_RATE = "Exchange Rate";
    private static final String URL_EXCHANGE_RATE = "/api/exchange-rate";

    private final ExchangeRateService exchangeRateService;

    // create single data
    @PostMapping(path = "create")
    public ResponseEntity<ResponseDTO<CreateExchangeRateListResponse>> createSingleData(@RequestBody CreateExchangeRateRequest createExchangeRateRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint(URL_EXCHANGE_RATE + "/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_EXCHANGE_RATE)
                .build();
        CreateExchangeRateListResponse createResponse = exchangeRateService.createSingleData(createExchangeRateRequest, dataChangeDTO);
        ResponseDTO<CreateExchangeRateListResponse> response = ResponseDTO.<CreateExchangeRateListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // update single data
    @PutMapping(path = "/update")
    public ResponseEntity<ResponseDTO<UpdateExchangeRateListResponse>> updateSingleData(@RequestBody UpdateExchangeRateRequest updateExchangeRateRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint(URL_EXCHANGE_RATE + "/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_EXCHANGE_RATE)
                .build();
        UpdateExchangeRateListResponse updateResponse = exchangeRateService.updateSingleData(updateExchangeRateRequest, dataChangeDTO);
        ResponseDTO<UpdateExchangeRateListResponse> response = ResponseDTO.<UpdateExchangeRateListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // update approve
    @PutMapping(path = "/update/approve")
    public ResponseEntity<ResponseDTO<UpdateExchangeRateListResponse>> updateApprove(@RequestBody UpdateExchangeRateRequest updateExchangeRateRequest) {
        UpdateExchangeRateListResponse exchangeRateApprove = exchangeRateService.updateApprove(updateExchangeRateRequest);
        ResponseDTO<UpdateExchangeRateListResponse> response = ResponseDTO.<UpdateExchangeRateListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(exchangeRateApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    // delete all
    @DeleteMapping(path = "/all")
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String deleteStatus = exchangeRateService.deleteAll();
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteStatus)
                .build();
        return ResponseEntity.ok(response);
    }

    // get all
    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<ExchangeRateDTO>>> getAll() {
        List<ExchangeRateDTO> exchangeRateDTOList = exchangeRateService.getAll();
        ResponseDTO<List<ExchangeRateDTO>> response = ResponseDTO.<List<ExchangeRateDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(exchangeRateDTOList)
                .build();
        return ResponseEntity.ok(response);
    }

}
