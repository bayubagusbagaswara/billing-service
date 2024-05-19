package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.*;
import com.bayu.billingservice.service.InvestmentManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/investment-management")
@RequiredArgsConstructor
@Slf4j
public class InvestmentManagementController {

    private static final String MENU_INVESTMENT_MANAGEMENT = "Investment Management";

    private final InvestmentManagementService investmentManagementService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<InvestmentManagementResponse>> create(@RequestBody CreateInvestmentManagementRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/investment-management/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_INVESTMENT_MANAGEMENT)
                .build();
        InvestmentManagementResponse createInvestmentManagementListResponse = investmentManagementService.createSingleData(request, dataChangeDTO);
        ResponseDTO<InvestmentManagementResponse> response = ResponseDTO.<InvestmentManagementResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create-list")
    public ResponseEntity<ResponseDTO<InvestmentManagementResponse>> createList(@RequestBody CreateInvestmentManagementListRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/investment-management/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_INVESTMENT_MANAGEMENT)
                .build();
        InvestmentManagementResponse list = investmentManagementService.createMultipleData(request, dataChangeDTO);

        ResponseDTO<InvestmentManagementResponse> response = ResponseDTO.<InvestmentManagementResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .payload(list)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create/approve")
    public ResponseEntity<ResponseDTO<InvestmentManagementResponse>> createListApprove(@RequestBody InvestmentManagementApproveRequest request) {
        InvestmentManagementResponse listApprove = investmentManagementService.createSingleApprove(request);
        ResponseDTO<InvestmentManagementResponse> response = ResponseDTO.<InvestmentManagementResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/updateById")
    public ResponseEntity<ResponseDTO<InvestmentManagementResponse>> updateById(@RequestBody UpdateInvestmentManagementRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/investment-management/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_INVESTMENT_MANAGEMENT)
                .build();
        InvestmentManagementResponse updateInvestmentManagementListResponse = investmentManagementService.updateSingleData(request, dataChangeDTO);
        ResponseDTO<InvestmentManagementResponse> response = ResponseDTO.<InvestmentManagementResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update-list")
    public ResponseEntity<ResponseDTO<InvestmentManagementResponse>> updateList(@RequestBody UpdateInvestmentManagementListRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/investment-management/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_INVESTMENT_MANAGEMENT)
                .build();
        InvestmentManagementResponse updateInvestmentManagementListResponse = investmentManagementService.updateMultipleData(request, dataChangeDTO);
        ResponseDTO<InvestmentManagementResponse> response = ResponseDTO.<InvestmentManagementResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update/approve")
    public ResponseEntity<ResponseDTO<InvestmentManagementResponse>> updateListApprove(@RequestBody InvestmentManagementApproveRequest request) {
        InvestmentManagementResponse updateInvestmentManagementListResponse = investmentManagementService.updateSingleApprove(request);
        ResponseDTO<InvestmentManagementResponse> response = ResponseDTO.<InvestmentManagementResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<ResponseDTO<InvestmentManagementResponse>> delete(@RequestBody DeleteInvestmentManagementRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.DELETE.name())
                .endpoint("/api/investment-management/delete/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_INVESTMENT_MANAGEMENT)
                .build();
        InvestmentManagementResponse deleteInvestmentManagementListResponse = investmentManagementService.deleteSingleData(request, dataChangeDTO);
        ResponseDTO<InvestmentManagementResponse> response = ResponseDTO.<InvestmentManagementResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/delete/approve")
    public ResponseEntity<ResponseDTO<InvestmentManagementResponse>> deleteApprove(@RequestBody InvestmentManagementApproveRequest request) {
        InvestmentManagementResponse deleteInvestmentManagementListResponse = investmentManagementService.deleteSingleApprove(request);
        ResponseDTO<InvestmentManagementResponse> response = ResponseDTO.<InvestmentManagementResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/all")
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String status = investmentManagementService.deleteAll();
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<InvestmentManagementDTO>>> getAll() {
        List<InvestmentManagementDTO> investmentManagementDTOList = investmentManagementService.getAll();
        ResponseDTO<List<InvestmentManagementDTO>> response = ResponseDTO.<List<InvestmentManagementDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(investmentManagementDTOList)
                .build();
        return ResponseEntity.ok(response);
    }
}
