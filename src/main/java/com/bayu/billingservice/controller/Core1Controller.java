package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.service.CoreType1Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/core-1")
@RequiredArgsConstructor
public class Core1Controller {

    private final CoreType1Service calculateService;

}
