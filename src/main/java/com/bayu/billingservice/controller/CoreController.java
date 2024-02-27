package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.service.Core1Service;
import com.bayu.billingservice.service.Core2Service;
import com.bayu.billingservice.util.BillingTypeConstant;
import com.bayu.billingservice.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/api/billing/core")
public class CoreController {

    private final Core1Service core1Service;
    private final Core2Service core2Service;

    public CoreController(Core1Service core1Service, Core2Service core2Service) {
        this.core1Service = core1Service;
        this.core2Service = core2Service;
    }

    @GetMapping(path = "/generate")
    public ResponseEntity<ResponseDTO<String>> generate(
            @RequestParam("category") String category,
            @RequestParam("type") String type,
            @RequestParam("monthYear") String monthYear
    ) {

        String categoryUpperCase = category.toUpperCase();
        String typeUppercase = StringUtil.replaceBlanksWithUnderscores(type);
        log.info("Start generate Category : {}, Type : {}, Month Year : {}", categoryUpperCase, typeUppercase, monthYear);

        ResponseDTO<String> response = new ResponseDTO<>();

        if (categoryUpperCase.equalsIgnoreCase(BillingTypeConstant.CORE_CATEGORY)) {
            if (BillingTypeConstant.CORE_TYPE_1.equalsIgnoreCase(typeUppercase)) {
                core1Service.calculate(categoryUpperCase, typeUppercase, monthYear);
            } else if (BillingTypeConstant.CORE_TYPE_2.equalsIgnoreCase(typeUppercase)) {
                core2Service.calculate(categoryUpperCase, typeUppercase, monthYear);
            } else {
                response.setCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setPayload("Type is not Billing Core");
            }
        } else {
            response.setCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setPayload("Category is not Billing Core");
        }

        return ResponseEntity.ok().body(response);
    }

}
