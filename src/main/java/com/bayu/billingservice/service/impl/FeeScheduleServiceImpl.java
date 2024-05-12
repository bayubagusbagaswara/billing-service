package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.repository.FeeScheduleRepository;
import com.bayu.billingservice.service.FeeScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeScheduleServiceImpl implements FeeScheduleService {

    private final FeeScheduleRepository feeScheduleRepository;
}
