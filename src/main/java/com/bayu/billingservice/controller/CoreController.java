package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.service.Core1Service;
import com.bayu.billingservice.util.BillingTypeConstant;
import com.bayu.billingservice.util.StringUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/billing/core")
public class CoreController {

    private final Core1Service core1Service;

    public CoreController(Core1Service core1Service) {
        this.core1Service = core1Service;
    }

    @GetMapping(path = "/generate")
    public ResponseEntity<ResponseDTO<String>> generate(
            @RequestParam("category") String category,
            @RequestParam("type") String type,
            @RequestParam("monthYear") String monthYear
    ) {

        if (category.equalsIgnoreCase(BillingTypeConstant.CORE_CATEGORY)
                && BillingTypeConstant.CORE_TYPE_1.equalsIgnoreCase(StringUtil.replaceBlanksWithUnderscores(type))) {

        }

        return null;
    }
}
