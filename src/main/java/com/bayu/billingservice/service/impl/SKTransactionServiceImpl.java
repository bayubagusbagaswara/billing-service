package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.repository.SkTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SKTransactionServiceImpl {

    private final SkTransactionRepository skTransactionRepository;
}
