package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.model.SfValRgDaily;
import com.bayu.billingservice.model.SfValRgMonthly;
import com.bayu.billingservice.service.SfValRgMonthlyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/sf-val/monthly")
@RequiredArgsConstructor
public class SfValRgMonthlyController {

    @Value("${file.path.sf-val-rg-monthly}")
    private String filePath;

    private final SfValRgMonthlyService sfValRgMonthlyService;

    @GetMapping(path = "/read-insert")
    public ResponseEntity<ResponseDTO<String>> readAndInsertToDB(@Param("monthYear") String monthYear) {
        String status = sfValRgMonthlyService.readFileAndInsertToDB(filePath, monthYear);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<SfValRgMonthly>>> getAll() {
        List<SfValRgMonthly> sfValRgMonthlyList = sfValRgMonthlyService.getAll();

        ResponseDTO<List<SfValRgMonthly>> response = ResponseDTO.<List<SfValRgMonthly>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfValRgMonthlyList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String status = sfValRgMonthlyService.deleteAll();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
