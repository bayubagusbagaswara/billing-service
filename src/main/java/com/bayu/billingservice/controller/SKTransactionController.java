package com.bayu.billingservice.controller;

import com.bayu.billingservice.service.SKTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/sktran")
@RequiredArgsConstructor
public class SKTransactionController {

    private final SKTransactionService skTransactionService;
}
