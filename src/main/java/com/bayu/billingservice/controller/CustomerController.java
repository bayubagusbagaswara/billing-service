package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private static final String MENU_CUSTOMER = "Customer";

    private final CustomerService customerService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<CustomerResponse>> create(@RequestBody CreateCustomerRequest request, HttpServletRequest servletRequest) {
        String clientIp = getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/customer/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_CUSTOMER)
                .build();
        CustomerResponse createCustomerListResponse = customerService.createSingleData(request, dataChangeDTO);
        ResponseDTO<CustomerResponse> response = ResponseDTO.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createCustomerListResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create-list")
    public ResponseEntity<ResponseDTO<CustomerResponse>> createList(@RequestBody CreateCustomerListRequest request, HttpServletRequest servletRequest) {
        String clientIp = getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/customer/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_CUSTOMER)
                .build();
        CustomerResponse createCustomerListResponse = customerService.createMultipleData(request, dataChangeDTO);
        ResponseDTO<CustomerResponse> response = ResponseDTO.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createCustomerListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create/approve")
    public ResponseEntity<ResponseDTO<CustomerResponse>> createApprove(@RequestBody CustomerApproveRequest request, HttpServletRequest servletRequest) {
        String clientIp = getClientIp(servletRequest);
        CustomerResponse createCustomerListResponse = customerService.createSingleApprove(request, clientIp);
        ResponseDTO<CustomerResponse> response = ResponseDTO.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createCustomerListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/updateById")
    public ResponseEntity<ResponseDTO<CustomerResponse>> updateById(@RequestBody UpdateCustomerRequest updateCustomerRequest, HttpServletRequest servletRequest) {
        String clientIp = getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/customer/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_CUSTOMER)
                .build();
        CustomerResponse customerResponse = customerService.updateSingleData(updateCustomerRequest, dataChangeDTO);
        ResponseDTO<CustomerResponse> response = ResponseDTO.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(customerResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update-list")
    public ResponseEntity<ResponseDTO<CustomerResponse>> updateList(@RequestBody UpdateCustomerListRequest updateCustomerListRequest, HttpServletRequest servletRequest) {
        String clientIp = getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/customer/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_CUSTOMER)
                .build();
        CustomerResponse updateCustomerListResponse = customerService.updateMultipleData(updateCustomerListRequest, dataChangeDTO);
        ResponseDTO<CustomerResponse> response = ResponseDTO.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateCustomerListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update/approve")
    public ResponseEntity<ResponseDTO<CustomerResponse>> updateApprove(@RequestBody CustomerApproveRequest request, HttpServletRequest servletRequest) {
        String clientIp = getClientIp(servletRequest);
        CustomerResponse customerResponse = customerService.updateSingleApprove(request, clientIp);
        ResponseDTO<CustomerResponse> response = ResponseDTO.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(customerResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/deleteById")
    public ResponseEntity<ResponseDTO<CustomerResponse>> deleteById(@RequestBody DeleteCustomerRequest deleteCustomerRequest, HttpServletRequest servletRequest) {
        String clientIp = getClientIp(servletRequest);
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .inputIPAddress(clientIp)
                .methodHttp(HttpMethod.DELETE.name())
                .endpoint("/api/customer/delete/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_CUSTOMER)
                .build();
        CustomerResponse customerResponse = customerService.deleteSingleData(deleteCustomerRequest, dataChangeDTO);
        ResponseDTO<CustomerResponse> response = ResponseDTO.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(customerResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/delete/approve")
    public ResponseEntity<ResponseDTO<CustomerResponse>> deleteApprove(@RequestBody CustomerApproveRequest approveRequest, HttpServletRequest servletRequest) {
        String clientIp = getClientIp(servletRequest);
        CustomerResponse customerResponse = customerService.deleteSingleApprove(approveRequest, clientIp);
        ResponseDTO<CustomerResponse> response = ResponseDTO.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(customerResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<CustomerDTO>>> getAll() {
        List<CustomerDTO> customerDTOList = customerService.getAll();
        ResponseDTO<List<CustomerDTO>> response = ResponseDTO.<List<CustomerDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(customerDTOList)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> delete() {
        String status = customerService.deleteAll();
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();
        return ResponseEntity.ok().body(response);
    }

    public String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        log.info("Client IP Address: {}", clientIp);
        return clientIp;
    }

}
