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

@RestController
@RequestMapping(path = "/api/investment-management")
@RequiredArgsConstructor
@Slf4j
public class InvestmentManagementController {

    private static final String MENU_INVESTMENT_MANAGEMENT = "Investment Management";

    private final InvestmentManagementService investmentManagementService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<CreateInvestmentManagementListResponse>> create(@RequestBody CreateInvestmentManagementRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/investment-management/create-list/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_INVESTMENT_MANAGEMENT)
                .build();
        CreateInvestmentManagementListResponse createInvestmentManagementListResponse = investmentManagementService.create(request, dataChangeDTO);
        ResponseDTO<CreateInvestmentManagementListResponse> response = ResponseDTO.<CreateInvestmentManagementListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create-list")
    public ResponseEntity<ResponseDTO<CreateInvestmentManagementListResponse>> createList(@RequestBody CreateInvestmentManagementListRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/investment-management/create-list/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_INVESTMENT_MANAGEMENT)
                .build();
        CreateInvestmentManagementListResponse list = investmentManagementService.createList(request, dataChangeDTO);

        ResponseDTO<CreateInvestmentManagementListResponse> response = ResponseDTO.<CreateInvestmentManagementListResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .payload(list)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create-list/approve")
    public ResponseEntity<ResponseDTO<CreateInvestmentManagementListResponse>> createListApprove(@RequestBody CreateInvestmentManagementListRequest request) {
        CreateInvestmentManagementListResponse listApprove = investmentManagementService.createListApprove(request);
        ResponseDTO<CreateInvestmentManagementListResponse> response = ResponseDTO.<CreateInvestmentManagementListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/updateById")
    public ResponseEntity<ResponseDTO<UpdateInvestmentManagementListResponse>> updateById(@RequestBody UpdateInvestmentManagementRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/investment-management/update-list/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_INVESTMENT_MANAGEMENT)
                .build();
        UpdateInvestmentManagementListResponse updateInvestmentManagementListResponse = investmentManagementService.updateById(request, dataChangeDTO);
        ResponseDTO<UpdateInvestmentManagementListResponse> response = ResponseDTO.<UpdateInvestmentManagementListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update-list")
    public ResponseEntity<ResponseDTO<UpdateInvestmentManagementListResponse>> updateList(@RequestBody UpdateInvestmentManagementListRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/investment-management/update-list/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_INVESTMENT_MANAGEMENT)
                .build();
        UpdateInvestmentManagementListResponse updateInvestmentManagementListResponse = investmentManagementService.updateList(request, dataChangeDTO);
        ResponseDTO<UpdateInvestmentManagementListResponse> response = ResponseDTO.<UpdateInvestmentManagementListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update-list/approve")
    public ResponseEntity<ResponseDTO<UpdateInvestmentManagementListResponse>> updateListApprove(@RequestBody UpdateInvestmentManagementListRequest request) {
        UpdateInvestmentManagementListResponse updateInvestmentManagementListResponse = investmentManagementService.updateListApprove(request);
        ResponseDTO<UpdateInvestmentManagementListResponse> response = ResponseDTO.<UpdateInvestmentManagementListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateInvestmentManagementListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

}
