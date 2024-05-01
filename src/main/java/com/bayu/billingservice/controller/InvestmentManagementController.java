package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListRequest;
import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementListResponse;
import com.bayu.billingservice.dto.investmentmanagement.CreateInvestmentManagementRequest;
import com.bayu.billingservice.service.InvestmentManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/investment-management")
@RequiredArgsConstructor
@Slf4j
public class InvestmentManagementController {

    private final InvestmentManagementService investmentManagementService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<CreateInvestmentManagementListResponse>> create(@RequestBody CreateInvestmentManagementRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/investment-management/create-list/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu("Investment Management")
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
                .menu("Investment Management")
                .build();
        CreateInvestmentManagementListResponse list = investmentManagementService.createList(request, dataChangeDTO);

        ResponseDTO<CreateInvestmentManagementListResponse> response = ResponseDTO.<CreateInvestmentManagementListResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .payload(list)
                .build();

        return ResponseEntity.ok(response);
    }
}
