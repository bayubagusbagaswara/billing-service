package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.core.Core1DTO;

import java.util.List;

public interface Core1Service {

    // response data
    List<Core1DTO> calculate(String category, String type, String monthYear);

    // Response message status
    String calculate1();

}
