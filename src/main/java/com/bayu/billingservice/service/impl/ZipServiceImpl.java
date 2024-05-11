package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.service.ZipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipServiceImpl implements ZipService {

    private static final Logger log = LoggerFactory.getLogger(ZipServiceImpl.class);

    public void zipFolder(String sourceFolderPath, String outputZipPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputZipPath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            addFolderToZip(sourceFolderPath, sourceFolderPath, zos);

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void addFolderToZip(String folderPath, String sourceFolder, ZipOutputStream zos) throws IOException {
        File folder = new File(folderPath);

        // Periksa apakah folder ada dan dapat diakses
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            // Periksa apakah hasil listFiles() tidak null
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        addFolderToZip(file.getAbsolutePath(), sourceFolder, zos);
                    } else {
                        addToZip(file.getAbsolutePath(), sourceFolder, zos);
                    }
                }
            } else {
                log.info("Tidak ada file yang dapat diakses dalam folder: {}", folderPath);
            }
        } else {
            log.info("Folder tidak ditemukan atau tidak dapat diakses: {}", folderPath);
        }
    }

    private void addToZip(String filePath, String sourceFolder, ZipOutputStream zos) throws IOException {
        String zipFilePath = filePath.substring(sourceFolder.length() + 1);
        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);

        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        } finally {
            zos.closeEntry(); // Tutup entry dalam ZIP setelah selesai menulis
        }
    }
}
