package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.service.SfValRgDailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/sf-val/daily")
@RequiredArgsConstructor
public class SfValRgDailyController {

    @Value("${file.path.sf-val-rg-daily}")
    private String filePath;

    private final SfValRgDailyService sfValRgDailyService;

    @GetMapping(path = "/read-insert")
    public ResponseEntity<ResponseDTO<String>> readAndInsertToDB() {
        String status = sfValRgDailyService.readFileAndInsertToDB(filePath);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<SfValRgDaily>>> getAll() {
        List<SfValRgDaily> sfValRgDailyList = sfValRgDailyService.getAll();

        ResponseDTO<List<SfValRgDaily>> response = ResponseDTO.<List<SfValRgDaily>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfValRgDailyList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String status = sfValRgDailyService.deleteAll();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
