package com.bayu.billingservice.service;

import java.io.IOException;

public interface ZipService {

    void zipFolder(String sourceFolderPath, String outputZipPath) throws IOException;
}
