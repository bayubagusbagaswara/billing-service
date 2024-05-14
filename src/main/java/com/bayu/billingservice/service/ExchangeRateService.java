package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.dto.exchangerate.*;

import java.util.List;

public interface ExchangeRateService {

    ExchangeRateDTO create(CreateExchangeRateRequest request);

    List<ExchangeRateDTO> getAll();

    ExchangeRateDTO getLatestDataByCurrency(String currency);

    ExchangeRateDTO getLatestData();

    ExchangeRateResponse createSingleData(CreateExchangeRateRequest createExchangeRateRequest, BillingDataChangeDTO dataChangeDTO);

    ExchangeRateResponse updateSingleData(UpdateExchangeRateRequest updateExchangeRateRequest, BillingDataChangeDTO dataChangeDTO);

    ExchangeRateResponse updateApprove(ExchangeRateApproveRequest approveRequest);

    String deleteAll();
}
