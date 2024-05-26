package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.*;
import com.bayu.billingservice.service.FeeScheduleService;
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
@RequestMapping(path = "/api/fee-schedule")
@RequiredArgsConstructor
@Slf4j
public class FeeScheduleController {

    private static final String MENU_FEE_SCHEDULE = "Fee Schedule";

    private final FeeScheduleService feeScheduleService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<FeeScheduleResponse>> create(@RequestBody CreateFeeScheduleRequest createFeeScheduleRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/fee-schedule/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_SCHEDULE)
                .build();
        FeeScheduleResponse createResponse = feeScheduleService.createSingleData(createFeeScheduleRequest, dataChangeDTO);
        ResponseDTO<FeeScheduleResponse> response = ResponseDTO.<FeeScheduleResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create/approve")
    public ResponseEntity<ResponseDTO<FeeScheduleResponse>> createSingleApprove(@RequestBody FeeScheduleApproveRequest createFeeScheduleListRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        FeeScheduleResponse listApprove = feeScheduleService.createSingleApprove(createFeeScheduleListRequest, clientIp);
        ResponseDTO<FeeScheduleResponse> response = ResponseDTO.<FeeScheduleResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/updateById")
    public ResponseEntity<ResponseDTO<FeeScheduleResponse>> updateSingleData(@RequestBody UpdateFeeScheduleRequest updateFeeScheduleRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/fee-schedule/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_SCHEDULE)
                .build();
        FeeScheduleResponse updateResponse = feeScheduleService.updateSingleData(updateFeeScheduleRequest, dataChangeDTO);
        ResponseDTO<FeeScheduleResponse> response = ResponseDTO.<FeeScheduleResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update/approve")
    public ResponseEntity<ResponseDTO<FeeScheduleResponse>> updateSingleApprove(@RequestBody FeeScheduleApproveRequest updateFeeScheduleListRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        FeeScheduleResponse listApprove = feeScheduleService.updateSingleApprove(updateFeeScheduleListRequest, clientIp);
        ResponseDTO<FeeScheduleResponse> response = ResponseDTO.<FeeScheduleResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/deleteById")
    public ResponseEntity<ResponseDTO<FeeScheduleResponse>> deleteSingleData(@RequestBody DeleteFeeScheduleRequest deleteFeeScheduleRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.DELETE.name())
                .endpoint("/api/fee-schedule/delete/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_SCHEDULE)
                .build();
        FeeScheduleResponse deleteResponse = feeScheduleService.deleteSingleData(deleteFeeScheduleRequest, dataChangeDTO);
        ResponseDTO<FeeScheduleResponse> response = ResponseDTO.<FeeScheduleResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/delete/approve")
    public ResponseEntity<ResponseDTO<FeeScheduleResponse>> deleteMultipleApprove(@RequestBody FeeScheduleApproveRequest deleteFeeScheduleListRequest, HttpServletRequest servletRequest) {
        String clientIp = ClientIPUtil.getClientIp(servletRequest);
        FeeScheduleResponse listApprove = feeScheduleService.deleteSingleApprove(deleteFeeScheduleListRequest, clientIp);
        ResponseDTO<FeeScheduleResponse> response = ResponseDTO.<FeeScheduleResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/all")
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String deleteStatus = feeScheduleService.deleteAll();
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteStatus)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<FeeScheduleDTO>>> getAll() {
        List<FeeScheduleDTO> feeScheduleDTOList = feeScheduleService.getAll();
        ResponseDTO<List<FeeScheduleDTO>> response = ResponseDTO.<List<FeeScheduleDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(feeScheduleDTOList)
                .build();
        return ResponseEntity.ok(response);
    }

}
