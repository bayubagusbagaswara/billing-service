package com.bayu.billingservice.controller;

import com.bayu.billingservice.service.CoreType1Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/billing/core")
@RequiredArgsConstructor
@Slf4j
public class CoreController {

    private final CoreType1Service coreType1Service;
}
