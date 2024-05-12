package com.bayu.billingservice.controller;

import com.bayu.billingservice.service.AssetTransferCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/asset-transfer-customer")
@RequiredArgsConstructor
@Slf4j
public class AssetTransferCustomerController {

    private final AssetTransferCustomerService assetTransferCustomerService;

    // TIDAK ADA CREATE LIST

    // Create Single Data

    // Create Multiple Approve

    // Update Single Data

    // Update Multiple Data

    // Update Multiple Approve

    // Delete Single Data

    // Delete Multiple Approve

}
