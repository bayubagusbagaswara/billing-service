package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.sellingagent.*;
import com.bayu.billingservice.service.SellingAgentService;
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
@RequestMapping(path = "/api/selling-agent")
@RequiredArgsConstructor
@Slf4j
public class SellingAgentController {

    private static final String MENU_SELLING_AGENT = "Selling Agent";

    private final SellingAgentService sellingAgentService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<SellingAgentResponse>> createSingleData(@RequestBody CreateSellingAgentRequest createSellingAgentRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputId(clientIp)
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/selling-agent/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_SELLING_AGENT)
                .build();
        SellingAgentResponse createResponse = sellingAgentService.createSingleData(createSellingAgentRequest, dataChangeDTO);
        ResponseDTO<SellingAgentResponse> response = ResponseDTO.<SellingAgentResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create/approve")
    public ResponseEntity<ResponseDTO<SellingAgentResponse>> createSingleApprove(@RequestBody SellingAgentApproveRequest createSellingAgentListRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        SellingAgentResponse listApprove = sellingAgentService.createSingleApprove(createSellingAgentListRequest, clientIp);
        ResponseDTO<SellingAgentResponse> response = ResponseDTO.<SellingAgentResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/updateById")
    public ResponseEntity<ResponseDTO<SellingAgentResponse>> updateSingleData(@RequestBody UpdateSellingAgentRequest updateSellingAgentRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/selling-agent/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_SELLING_AGENT)
                .build();
        SellingAgentResponse updateResponse = sellingAgentService.updateSingleData(updateSellingAgentRequest, dataChangeDTO);
        ResponseDTO<SellingAgentResponse> response = ResponseDTO.<SellingAgentResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update/approve")
    public ResponseEntity<ResponseDTO<SellingAgentResponse>> updateSingleApprove(@RequestBody SellingAgentApproveRequest updateSellingAgentListRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        SellingAgentResponse listApprove = sellingAgentService.updateSingleApprove(updateSellingAgentListRequest, clientIp);
        ResponseDTO<SellingAgentResponse> response = ResponseDTO.<SellingAgentResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/deleteById")
    public ResponseEntity<ResponseDTO<SellingAgentResponse>> deleteSingleData(@RequestBody DeleteSellingAgentRequest deleteSellingAgentRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.DELETE.name())
                .endpoint("/api/selling-agent/delete/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_SELLING_AGENT)
                .build();
        SellingAgentResponse deleteResponse = sellingAgentService.deleteSingleData(deleteSellingAgentRequest, dataChangeDTO);
        ResponseDTO<SellingAgentResponse> response = ResponseDTO.<SellingAgentResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/delete/approve")
    public ResponseEntity<ResponseDTO<SellingAgentResponse>> deleteSingleApprove(@RequestBody SellingAgentApproveRequest deleteSellingAgentListRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        SellingAgentResponse listApprove = sellingAgentService.deleteSingleApprove(deleteSellingAgentListRequest, clientIp);
        ResponseDTO<SellingAgentResponse> response = ResponseDTO.<SellingAgentResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/all")
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String deleteStatus = sellingAgentService.deleteAll();
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteStatus)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<SellingAgentDTO>>> getAll() {
        List<SellingAgentDTO> sellingAgentDTOList = sellingAgentService.getAll();
        ResponseDTO<List<SellingAgentDTO>> response = ResponseDTO.<List<SellingAgentDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sellingAgentDTOList)
                .build();
        return ResponseEntity.ok(response);
    }

}
