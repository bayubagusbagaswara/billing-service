package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.exchangerate.*;
import com.bayu.billingservice.service.ExchangeRateService;
import com.bayu.billingservice.util.ClientIPUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<ExchangeRateResponse>> createSingleData(@RequestBody CreateExchangeRateRequest createExchangeRateRequest) {
        ExchangeRateResponse createResponse = exchangeRateService.createSingleData(createExchangeRateRequest);
        ResponseDTO<ExchangeRateResponse> response = ResponseDTO.<ExchangeRateResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<ResponseDTO<ExchangeRateResponse>> updateSingleData(@RequestBody UpdateExchangeRateRequest updateExchangeRateRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.PUT.name())
                .endpoint(URL_EXCHANGE_RATE + "/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_EXCHANGE_RATE)
                .build();
        ExchangeRateResponse updateResponse = exchangeRateService.updateSingleData(updateExchangeRateRequest, dataChangeDTO);
        ResponseDTO<ExchangeRateResponse> response = ResponseDTO.<ExchangeRateResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update/approve")
    public ResponseEntity<ResponseDTO<ExchangeRateResponse>> updateApprove(@RequestBody ExchangeRateApproveRequest updateExchangeRateRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        ExchangeRateResponse exchangeRateApprove = exchangeRateService.updateApprove(updateExchangeRateRequest, clientIp);
        ResponseDTO<ExchangeRateResponse> response = ResponseDTO.<ExchangeRateResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(exchangeRateApprove)
                .build();
        return ResponseEntity.ok(response);
    }

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
