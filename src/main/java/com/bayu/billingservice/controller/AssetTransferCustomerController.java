package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.assettransfercustomer.*;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.service.AssetTransferCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/asset-transfer-customer")
@RequiredArgsConstructor
@Slf4j
public class AssetTransferCustomerController {

    private static final String MENU_ASSET_TRANSFER_CUSTOMER = "Asset Transfer Customer";
    private static final String URL_ASSET_TRANSFER_CUSTOMER = "/api/asset-transfer-customer";

    private final AssetTransferCustomerService assetTransferCustomerService;

    // TIDAK ADA CREATE LIST

    // Create Single Data
    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<AssetTransferCustomerResponse>> createSingleData(@RequestBody CreateAssetTransferCustomerRequest createAssetTransferCustomerRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.POST.name())
                .endpoint(URL_ASSET_TRANSFER_CUSTOMER + "/create/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_ASSET_TRANSFER_CUSTOMER)
                .build();
        AssetTransferCustomerResponse createResponse = assetTransferCustomerService.createSingleData(createAssetTransferCustomerRequest, dataChangeDTO);
        ResponseDTO<AssetTransferCustomerResponse> response = ResponseDTO.<AssetTransferCustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(createResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // Create Multiple Approve
    @PostMapping(path = "/create/approve")
    public ResponseEntity<ResponseDTO<AssetTransferCustomerResponse>> createSingleApprove(@RequestBody AssetTransferCustomerApproveRequest createAssetTransferCustomerListRequest) {
        AssetTransferCustomerResponse listApprove = assetTransferCustomerService.createSingleApprove(createAssetTransferCustomerListRequest);
        ResponseDTO<AssetTransferCustomerResponse> response = ResponseDTO.<AssetTransferCustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(listApprove)
                .build();
        return ResponseEntity.ok(response);
    }

    // Update Single Data
    @PutMapping(path = "/update")
    public ResponseEntity<ResponseDTO<AssetTransferCustomerResponse>> updateSingleData(@RequestBody UpdateAssetTransferCustomerRequest updateAssetTransferCustomerRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint(URL_ASSET_TRANSFER_CUSTOMER + "/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_ASSET_TRANSFER_CUSTOMER)
                .build();
        AssetTransferCustomerResponse updateResponse = assetTransferCustomerService.updateSingleData(updateAssetTransferCustomerRequest, dataChangeDTO);
        ResponseDTO<AssetTransferCustomerResponse> response = ResponseDTO.<AssetTransferCustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // Update Multiple Data
    @PutMapping(path = "/update-list")
    public ResponseEntity<ResponseDTO<AssetTransferCustomerResponse>> updateMultipleData(@RequestBody AssetTransferCustomerListRequest updateAssetTransferCustomerListRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.PUT.name())
                .endpoint(URL_ASSET_TRANSFER_CUSTOMER + "/update/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_ASSET_TRANSFER_CUSTOMER)
                .build();
        AssetTransferCustomerResponse updateListResponse = assetTransferCustomerService.updateMultipleData(updateAssetTransferCustomerListRequest, dataChangeDTO);
        ResponseDTO<AssetTransferCustomerResponse> response = ResponseDTO.<AssetTransferCustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // Update Multiple Approve
    @PutMapping(path = "/update/approve")
    public ResponseEntity<ResponseDTO<AssetTransferCustomerResponse>> updateSingleApprove(@RequestBody AssetTransferCustomerApproveRequest updateAssetTransferCustomerListRequest) {
        AssetTransferCustomerResponse updateListResponse = assetTransferCustomerService.updateSingleApprove(updateAssetTransferCustomerListRequest);
        ResponseDTO<AssetTransferCustomerResponse> response = ResponseDTO.<AssetTransferCustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(updateListResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // Delete Single Data
    @DeleteMapping(path = "/delete")
    public ResponseEntity<ResponseDTO<AssetTransferCustomerResponse>> deleteSingleData(@RequestBody DeleteAssetTransferCustomerRequest deleteAssetTransferCustomerRequest) {
        BillingDataChangeDTO dataChangeDTO = BillingDataChangeDTO.builder()
                .methodHttp(HttpMethod.DELETE.name())
                .endpoint(URL_ASSET_TRANSFER_CUSTOMER + "/delete/approve")
                .isRequestBody(true)
                .isRequestParam(false)
                .isPathVariable(false)
                .menu(MENU_ASSET_TRANSFER_CUSTOMER)
                .build();
        AssetTransferCustomerResponse deleteResponse = assetTransferCustomerService.deleteSingleData(deleteAssetTransferCustomerRequest, dataChangeDTO);
        ResponseDTO<AssetTransferCustomerResponse> response = ResponseDTO.<AssetTransferCustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // Delete Multiple Approve
    @DeleteMapping(path = "/delete/approve")
    public ResponseEntity<ResponseDTO<AssetTransferCustomerResponse>> deleteSingleData(@RequestBody AssetTransferCustomerApproveRequest deleteAssetTransferCustomerListRequest) {
        AssetTransferCustomerResponse deleteResponse = assetTransferCustomerService.deleteSingleApprove(deleteAssetTransferCustomerListRequest);
        ResponseDTO<AssetTransferCustomerResponse> response = ResponseDTO.<AssetTransferCustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    // hard delete all
    @DeleteMapping(path = "/all")
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String deleteStatus = assetTransferCustomerService.deleteAll();
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(deleteStatus)
                .build();
        return ResponseEntity.ok(response);
    }

    // get all
    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<AssetTransferCustomerDTO>>> getAll() {
        List<AssetTransferCustomerDTO> assetTransferCustomerDTOList = assetTransferCustomerService.getAll();
        ResponseDTO<List<AssetTransferCustomerDTO>> response = ResponseDTO.<List<AssetTransferCustomerDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(assetTransferCustomerDTOList)
                .build();
        return ResponseEntity.ok(response);
    }

}
