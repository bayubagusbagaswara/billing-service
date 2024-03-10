package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.core.Core1DTO;

import java.util.List;

public interface Core1CalculateService {

    List<Core1DTO> calculate(String category, String type, String monthYear);



}
