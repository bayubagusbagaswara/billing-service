package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.repository.AssetTransferCustomerRepository;
import com.bayu.billingservice.service.AssetTransferCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetTransferCustomerServiceImpl implements AssetTransferCustomerService {

    private final AssetTransferCustomerRepository assetTransferCustomerRepository;

}
