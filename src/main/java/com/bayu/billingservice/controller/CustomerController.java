package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.customer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.service.CustomerService;
import com.bayu.billingservice.service.CustomerV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerV2Service customerV2Service;
    private static final String MENU_CUSTOMER = "Customer";

    @PostMapping(path = "/create-test")
    public ResponseEntity<ResponseDTO<CustomerDTO>> createTest(@RequestBody CustomerDTO request) {
        CustomerDTO customerDTO = customerV2Service.testCreate(request);
        ResponseDTO<CustomerDTO> response = ResponseDTO.<CustomerDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully created customer with id: " + customerDTO.getId())
                .payload(customerDTO)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/all-test")
    public ResponseEntity<ResponseDTO<List<CustomerDTO>>> getAllTest() {

        List<CustomerDTO> customerDTOList = customerV2Service.getAllTest();

        ResponseDTO<List<CustomerDTO>> response = ResponseDTO.<List<CustomerDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(customerDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }


    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<CreateCustomerListResponse>> create(@RequestBody CreateCustomerRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/customer/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_CUSTOMER)
                .build();
        CreateCustomerListResponse createCustomerListResponse = customerService.createSingleData(request, dataChangeDTO);

        ResponseDTO<CreateCustomerListResponse> response = ResponseDTO.<CreateCustomerListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createCustomerListResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create-list")
    public ResponseEntity<ResponseDTO<CreateCustomerListResponse>> createList(@RequestBody CreateCustomerListRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint("/api/customer/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_CUSTOMER)
                .build();
        CreateCustomerListResponse createCustomerListResponse = customerService.createMultipleData(request, dataChangeDTO);
        ResponseDTO<CreateCustomerListResponse> response = ResponseDTO.<CreateCustomerListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createCustomerListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/create/approve")
    public ResponseEntity<ResponseDTO<CreateCustomerListResponse>> createApprove(@RequestBody CreateCustomerListRequest request) {
        CreateCustomerListResponse createCustomerListResponse = customerService.createMultipleApprove(request);
        ResponseDTO<CreateCustomerListResponse> response = ResponseDTO.<CreateCustomerListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createCustomerListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update-list")
    public ResponseEntity<ResponseDTO<UpdateCustomerListResponse>> updateList(@RequestBody UpdateCustomerListRequest request) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint("/api/customer/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_CUSTOMER)
                .build();
        UpdateCustomerListResponse updateCustomerListResponse = customerService.updateMultipleData(request, dataChangeDTO);
        ResponseDTO<UpdateCustomerListResponse> response = ResponseDTO.<UpdateCustomerListResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateCustomerListResponse)
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

}
