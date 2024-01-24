package com.bayu.billingservice.controller;

import com.bayu.billingservice.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/billings")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;
}
