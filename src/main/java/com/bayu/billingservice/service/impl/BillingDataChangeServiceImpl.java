package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.model.BillingDataChange;
import com.bayu.billingservice.repository.BillingDataChangeRepository;
import com.bayu.billingservice.service.BillingDataChangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingDataChangeServiceImpl implements BillingDataChangeService {

    private final BillingDataChangeRepository dataChangeRepository;

    @Override
    public List<BillingDataChange> getAll() {
        return dataChangeRepository.findAll();
    }

    @Override
    public String deleteAll() {
        dataChangeRepository.deleteAll();
        return "Successfully delete all data change";
    }
}
