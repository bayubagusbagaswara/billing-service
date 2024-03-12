package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.CoreCalculateRequest;
import com.bayu.billingservice.dto.core.Core1DTO;

import java.util.List;

public interface Core1GeneratePDFService {

    List<Core1DTO> getAll();

    String generatePDF(CoreCalculateRequest request);
}
