package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.core.Core2DTO;

import java.util.List;

public interface Core2CalculateService {

    List<Core2DTO> calculate(String category, String type, String monthYear);

    String calculate1(String category, String type, String monthYear);
}
