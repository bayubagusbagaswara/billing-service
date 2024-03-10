package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.BillingNumberDTO;

import java.util.List;

public interface BillingNumberService {

    String saveToAll(List<String> numberList);

    Integer getMaxSequenceNumberByMonthAndYear(String month, int year);

    List<BillingNumberDTO> getAll();

    List<String> generateNumberList(int billingSize, String month, int year);
}
