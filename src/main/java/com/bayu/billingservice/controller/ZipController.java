package com.bayu.billingservice.controller;

import com.bayu.billingservice.dto.ResponseDTO;
import com.bayu.billingservice.dto.zip.ZipRequest;
import com.bayu.billingservice.service.ZipService;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @GetMapping("/downloadZip")
    public ResponseEntity<InputStreamResource> downloadZip(@RequestBody ZipRequest zipRequest) {
        String monthYear = zipRequest.getMonthYear();
        Map<String, String> stringStringMap = convertDateUtil.extractMonthYearInformation(monthYear);
        String monthValue = stringStringMap.get("monthValue");
        String year = stringStringMap.get("year");
        String folderPath;
        // Set output zip file path
        String zipFileName;
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
            folderPath = basePathBilling + year + monthValue;
            zipFileName = zipRequest.getCategory().toUpperCase() + "_" + year + monthValue + ".zip";
        } else {
            folderPath = basePathBilling + year + monthValue + DELIMITER + zipRequest.getInvestmentManagementName();
            zipFileName = zipRequest.getCategory().toUpperCase() + "_" +  year + monthValue + "_" + zipRequest.getInvestmentManagementName() + ".zip";
        }

        try {
            Path folder = Paths.get(folderPath);
            if (!Files.exists(folder) || !Files.isDirectory(folder)) {
                return ResponseEntity.badRequest().body(null);
            }

            // Generate new name for the ZIP file
            // String zipFileName = "my_archive.zip"; // Ganti dengan nama yang diinginkan

            // Replace spaces with underscores in the file name
            zipFileName = zipFileName.replace(" ", "_");

            // Create a temporary file to write the zip content
            Path zipFilePath = Files.createTempFile("temp", ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                // Compress the folder contents recursively
                zipFolderContents(folder.toFile(), folder.getFileName().toString(), zos);

            } // Sumber daya (fos dan zos) akan otomatis ditutup setelah blok try

            // Prepare file download response with custom file name
            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFilePath.toFile()));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(Files.size(zipFilePath))
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    private void zipFolderContents(File folder, String parentFolderName, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipFolderContents(file, parentFolderName + "/" + file.getName(), zos);
                } else {
                    addToZip(parentFolderName + "/" + file.getName(), file, zos);
                }
            }
        }
    }

    private void addToZip(String entryName, File file, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
            zos.closeEntry();
        }
    }
}
