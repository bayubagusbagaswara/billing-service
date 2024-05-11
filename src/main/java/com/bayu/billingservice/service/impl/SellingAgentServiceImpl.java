package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.repository.SellingAgentRepository;
import com.bayu.billingservice.service.SellingAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellingAgentServiceImpl implements SellingAgentService {

    private final SellingAgentRepository sellingAgentRepository;

    @Override
    public boolean isCodeAlreadyExists(String sellingAgentCode) {
        return sellingAgentRepository.existsByCode(sellingAgentCode);
    }

}
