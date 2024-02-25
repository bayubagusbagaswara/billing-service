package com.bayu.billingservice.service.impl;

import com.bayu.billingservice.dto.exchangerate.CreateExchangeRateRequest;
import com.bayu.billingservice.dto.exchangerate.ExchangeRateDTO;
import com.bayu.billingservice.exception.DataNotFoundException;
import com.bayu.billingservice.model.ExchangeRate;
import com.bayu.billingservice.repository.ExchangeRateRepository;
import com.bayu.billingservice.service.ExchangeRateService;
import com.bayu.billingservice.util.ConvertBigDecimalUtil;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ExchangeRateDTO create(CreateExchangeRateRequest request) {
        LocalDate date = ConvertDateUtil.parseDateOrDefault(request.getDate(), formatter);
        BigDecimal value = ConvertBigDecimalUtil.parseBigDecimalOrDefault(request.getValue());

        ExchangeRate exchangeRate = ExchangeRate.builder()
                .date(date)
                .currency(request.getCurrency().toUpperCase())
                .value(value)
                .build();

        return mapToDTO(exchangeRateRepository.save(exchangeRate));
    }

    @Override
    public List<ExchangeRateDTO> getAll() {
        return mapToDTOList(exchangeRateRepository.findAll());
    }

    @Override
    public ExchangeRateDTO getLatestDataByCurrency(String currency) {
        ExchangeRate exchangeRate = exchangeRateRepository.findLatestExchangeRateByCurrency(currency)
                .orElseThrow(() -> new DataNotFoundException("Exchange Rate not found with currency : " + currency));
        return mapToDTO(exchangeRate);
    }

    @Override
    public ExchangeRateDTO getLatestData() {
        ExchangeRate exchangeRate = exchangeRateRepository.findLatestExchangeRate()
                .orElseThrow(() -> new DataNotFoundException("Exchange Rate not found"));
        return mapToDTO(exchangeRate);
    }

    private static ExchangeRateDTO mapToDTO(ExchangeRate exchangeRate) {
        return ExchangeRateDTO.builder()
                .id(exchangeRate.getId())
                .date(String.valueOf(exchangeRate.getDate()))
                .currency(exchangeRate.getCurrency())
                .value(String.valueOf(exchangeRate.getValue()))
                .build();
    }

    private static List<ExchangeRateDTO> mapToDTOList(List<ExchangeRate> exchangeRateList) {
        return exchangeRateList.stream()
                .map(ExchangeRateServiceImpl::mapToDTO)
                .toList();
    }

}
