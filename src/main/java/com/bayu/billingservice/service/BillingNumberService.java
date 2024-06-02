package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.billingnumber.BillingNumberDTO;

import java.util.List;

public interface BillingNumberService {

    String saveAll(List<String> numberList);

    String saveSingleNumber(String number);

    Integer getMaxSequenceNumberByMonthAndYear(String month, int year);

    List<BillingNumberDTO> getAll();

    List<String> generateNumberList(int billingSize, String month, int year);

    String generateSingleNumber(String month, int year);

    void deleteByBillingNumber(String billingNumber);
}
