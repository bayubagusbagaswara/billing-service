package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.iig.CreateSfValCoreIIGRequest;
import com.bayu.billingservice.model.SfValCoreIIG;
import com.bayu.billingservice.service.SfValCoreIIGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/sf-val/iig")
@RequiredArgsConstructor
public class SfValCoreIIGController {

    private final SfValCoreIIGService sfValCoreIIGService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<String>> create(@RequestBody CreateSfValCoreIIGRequest request) {
        String status = sfValCoreIIGService.create(request);
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<SfValCoreIIG>>> getAll() {
        List<SfValCoreIIG> sfValCoreIIGList = sfValCoreIIGService.getAll();
        ResponseDTO<List<SfValCoreIIG>> response = ResponseDTO.<List<SfValCoreIIG>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfValCoreIIGList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/aid-period")
    public ResponseEntity<ResponseDTO<List<SfValCoreIIG>>> getAllByAidAndMonthYear(@RequestParam("aid") String aid,
                                                                                   @RequestParam("monthYear") String monthYear) {
        List<SfValCoreIIG> sfValCoreIIGList = sfValCoreIIGService.getAllByAidAndMonthYear(aid, monthYear);
        ResponseDTO<List<SfValCoreIIG>> response = ResponseDTO.<List<SfValCoreIIG>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfValCoreIIGList)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
