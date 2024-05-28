package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.kseisafe.CreateKseiSafeRequest;
import com.bayu.billingservice.model.KseiSafekeepingFee;

import java.math.BigDecimal;
import java.util.List;

public interface KseiSafekeepingFeeService {

    List<KseiSafekeepingFee> create(List<CreateKseiSafeRequest> request);

    String readAndInsertToDB(String filePath, String monthYear);

    List<KseiSafekeepingFee> getAll();

    List<KseiSafekeepingFee> getByCustomerCode(String customerCode);

    BigDecimal calculateAmountFeeByCustomerCodeAndMonthAndYear(String customerCode, String month, int year);

    BigDecimal calculateAmountFeeForLast3Months(String customerCode, String month, int year);

    String deleteAll();

}
