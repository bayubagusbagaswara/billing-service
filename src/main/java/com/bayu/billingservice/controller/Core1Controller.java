package com.bayu.billingservice.controller;

import com.bayu.billingservice.service.Core1CalculateService;
import com.bayu.billingservice.service.Core1GeneratePDFService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/api/core-1")
@RequiredArgsConstructor
public class Core1Controller {

    private final Core1CalculateService calculateService;
    private final Core1GeneratePDFService generatePDFService;

}
