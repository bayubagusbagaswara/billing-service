package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.exchangerate.*;

import java.util.List;

public interface ExchangeRateService {

    boolean isCurrencyAlreadyExists(String currency);

    List<ExchangeRateDTO> getAll();

    ExchangeRateDTO getByCurrency(String currency);

    ExchangeRateResponse createSingleData(CreateExchangeRateRequest createExchangeRateRequest);

    ExchangeRateResponse updateSingleData(UpdateExchangeRateRequest updateExchangeRateRequest, BillingDataChangeDTO dataChangeDTO);

    ExchangeRateResponse updateApprove(ExchangeRateApproveRequest approveRequest, String clientIP);

    String deleteAll();
}
