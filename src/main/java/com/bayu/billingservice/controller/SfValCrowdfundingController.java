package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.model.SfValCrowdfunding;
import com.bayu.billingservice.service.SfValCrowdfundingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/crowdfunding")
@RequiredArgsConstructor
public class SfValCrowdfundingController {
    @Value("${file.path.crowdfunding}")
    private String filePath;

    private final SfValCrowdfundingService sfValCrowdfundingService;

    // read and insert
    @GetMapping(path = "/read-insert")
    public ResponseEntity<ResponseDTO<String>> readAndInsert() {
        String status = sfValCrowdfundingService.readAndInsertToDB(filePath);
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<SfValCrowdfunding>>> getAll() {
        List<SfValCrowdfunding> sfValCrowdfundingList = sfValCrowdfundingService.getAll();

        ResponseDTO<List<SfValCrowdfunding>> response = ResponseDTO.<List<SfValCrowdfunding>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfValCrowdfundingList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    // delete
//    @DeleteMapping
//    public ResponseEntity<ResponseDTO<String>> deleteAll() {
//        sfValCrowdFundingService
//    }
}
