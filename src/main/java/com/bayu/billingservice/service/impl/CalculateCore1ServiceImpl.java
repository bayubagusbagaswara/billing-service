package com.bayu.billingservice.service.impl;


import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.model.BillingCore;
import com.bayu.billingservice.service.CoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculateCore1ServiceImpl implements CoreService {

    @Override
    public String calculateCore1(CoreCalculateRequest request) {
        return null;
    }

    @Override
    public String calculateCore2(CoreCalculateRequest request) {
        return null;
    }

    @Override
    public String calculateCore3() {
        return null;
    }

    @Override
    public String calculateCore4() {
        return null;
    }

    @Override
    public List<BillingCore> getAll() {
        return null;
    }

    @Override
    public String deleteAll() {
        return null;
    }
}
