package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.exchangerate.*;

import java.util.List;

public interface ExchangeRateService {

    ExchangeRateDTO create(CreateExchangeRateRequest request);

    List<ExchangeRateDTO> getAll();

    ExchangeRateDTO getLatestDataByCurrency(String currency);

    ExchangeRateDTO getLatestData();

    CreateExchangeRateListResponse createSingleData(CreateExchangeRateRequest createExchangeRateRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateExchangeRateListResponse updateSingleData(UpdateExchangeRateRequest updateExchangeRateRequest, BillingDataChangeDTO dataChangeDTO);

    UpdateExchangeRateListResponse updateApprove(UpdateExchangeRateRequest updateExchangeRateRequest);

    String deleteAll();
}
