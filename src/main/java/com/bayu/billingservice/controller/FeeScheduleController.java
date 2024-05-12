package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.feeschedule.*;
import com.bayu.billingservice.service.FeeScheduleService;
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

    // TIDAK ADA CREATE LIST

    // Create Single Data
    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<CreateFeeScheduleListResponse>> create(@RequestBody CreateFeeScheduleRequest createFeeScheduleRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/fee-schedule/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_SCHEDULE)
                .build();
        CreateFeeScheduleListResponse createResponse = feeScheduleService.createSingleData(createFeeScheduleRequest, dataChangeDTO);
        ResponseDTO<CreateFeeScheduleListResponse> response = ResponseDTO.<CreateFeeScheduleListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // Create Multiple Approve
    @PostMapping(path = "/create/approve")
    public ResponseEntity<ResponseDTO<CreateFeeScheduleListResponse>> createMultipleApprove(@RequestBody CreateFeeScheduleListRequest createFeeScheduleListRequest) {
        CreateFeeScheduleListResponse listApprove = feeScheduleService.createMultipleApprove(createFeeScheduleListRequest);
        ResponseDTO<CreateFeeScheduleListResponse> response = ResponseDTO.<CreateFeeScheduleListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    // Update Single Data by Id
    @PutMapping(path = "/update")
    public ResponseEntity<ResponseDTO<UpdateFeeScheduleListResponse>> updateSingleData(@RequestBody UpdateFeeScheduleRequest updateFeeScheduleRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/fee-schedule/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_SCHEDULE)
                .build();
        UpdateFeeScheduleListResponse updateResponse = feeScheduleService.updateSingleData(updateFeeScheduleRequest, dataChangeDTO);
        ResponseDTO<UpdateFeeScheduleListResponse> response = ResponseDTO.<UpdateFeeScheduleListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // Update Multiple Data
    @PutMapping(path = "/update-list")
    public ResponseEntity<ResponseDTO<UpdateFeeScheduleListResponse>> updateMultipleData(@RequestBody UpdateFeeScheduleListRequest updateFeeScheduleListRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/fee-schedule/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_SCHEDULE)
                .build();
        UpdateFeeScheduleListResponse updateResponse = feeScheduleService.updateMultipleData(updateFeeScheduleListRequest, dataChangeDTO);
        ResponseDTO<UpdateFeeScheduleListResponse> response = ResponseDTO.<UpdateFeeScheduleListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // Update Multiple Approve
    @PutMapping(path = "/update/approve")
    public ResponseEntity<ResponseDTO<UpdateFeeScheduleListResponse>> updateMultipleApprove(@RequestBody UpdateFeeScheduleListRequest updateFeeScheduleListRequest) {
        UpdateFeeScheduleListResponse listApprove = feeScheduleService.updateMultipleApprove(updateFeeScheduleListRequest);
        ResponseDTO<UpdateFeeScheduleListResponse> response = ResponseDTO.<UpdateFeeScheduleListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    // Delete Single Data
    @DeleteMapping(path = "/delete")
    public ResponseEntity<ResponseDTO<DeleteFeeScheduleListResponse>> deleteSingleData(@RequestBody DeleteFeeScheduleRequest deleteFeeScheduleRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.DELETE.name())
                .endpoint("/api/fee-schedule/delete/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_FEE_SCHEDULE)
                .build();
        DeleteFeeScheduleListResponse deleteResponse = feeScheduleService.deleteSingleData(deleteFeeScheduleRequest, dataChangeDTO);
        ResponseDTO<DeleteFeeScheduleListResponse> response = ResponseDTO.<DeleteFeeScheduleListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // Delete Multiple Approve
    @DeleteMapping(path = "/delete/approve")
    public ResponseEntity<ResponseDTO<DeleteFeeScheduleListResponse>> deleteMultipleApprove(@RequestBody DeleteFeeScheduleListRequest deleteFeeScheduleListRequest) {
        DeleteFeeScheduleListResponse deleteResponse = feeScheduleService.deleteMultipleApprove(deleteFeeScheduleListRequest);
        ResponseDTO<DeleteFeeScheduleListResponse> response = ResponseDTO.<DeleteFeeScheduleListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // delete all hard delete
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

    // get all
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
