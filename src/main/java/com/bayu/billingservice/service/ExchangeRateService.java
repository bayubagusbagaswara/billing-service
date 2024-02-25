package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.exchangerate.CreateExchangeRateRequest;
import com.bayu.billingservice.dto.exchangerate.ExchangeRateDTO;

import java.util.List;

public interface ExchangeRateService {

    ExchangeRateDTO create(CreateExchangeRateRequest request);

    List<ExchangeRateDTO> getAll();

    ExchangeRateDTO getLatestDataByCurrency(String currency);

    ExchangeRateDTO getLatestData();
}
