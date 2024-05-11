package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.ZipRequest;
import com.bayu.billingservice.service.ZipService;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/zip")
@RequiredArgsConstructor
@Slf4j
public class ZipController {

    @Value("${base.path.billing.fund}")
    private String basePathBillingFund;

    @Value("${base.path.billing.core}")
    private String basePathBillingCore;

    @Value("${base.path.billing.retail}")
    private String basePathBillingRetail;


    private static final String DELIMITER = "/";

    private final ZipService zipService;
    private final ConvertDateUtil convertDateUtil;

    @PostMapping("/download-pdf-zip")
    public ResponseEntity<ResponseDTO<String>> downloadPdfZip(@RequestBody ZipRequest zipRequest) {
        try {
            String monthYear = zipRequest.getMonthYear();
            Map<String, String> stringStringMap = convertDateUtil.extractMonthYearInformation(monthYear);
            String monthValue = stringStringMap.get("monthValue");
            String year = stringStringMap.get("year");
            String sourceFolderPath;
            String outputZipPath;

            String basePathBilling;

            if ("FUND".equalsIgnoreCase(zipRequest.getCategory())) {
                basePathBilling = basePathBillingFund;
            } else if ("CORE".equalsIgnoreCase(zipRequest.getCategory())) {
                basePathBilling = basePathBillingCore;
            } else if ("RETAIL".equalsIgnoreCase(zipRequest.getCategory())) {
                basePathBilling = basePathBillingRetail;
            } else {
                basePathBilling = "";
            }

            if (zipRequest.getInvestmentManagementName().isEmpty()) {
                sourceFolderPath = basePathBilling + year + monthValue;
                outputZipPath = System.getProperty("user.home") + "/Downloads/" + zipRequest.getCategory().toUpperCase() + "_" + year + monthValue + ".zip";
            } else {
                sourceFolderPath = basePathBilling + year + monthValue + DELIMITER + zipRequest.getInvestmentManagementName();
                outputZipPath = System.getProperty("user.home") + "/Downloads/" + zipRequest.getCategory().toUpperCase() + "_" +  year + monthValue + "_" + zipRequest.getInvestmentManagementName() + ".zip";
            }

            zipService.zipFolder(sourceFolderPath, outputZipPath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .code(200)
                    .message("File zip berhasil diunduh")
                    .payload(outputZipPath.replace('/', '\\')) // Path file zip yang diunduh
                    .build();

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(responseDTO);
        } catch (IOException e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .code(500)
                    .message("Terjadi kesalahan saat mengunduh file zip")
                    .payload(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseDTO);
        }
    }
}
