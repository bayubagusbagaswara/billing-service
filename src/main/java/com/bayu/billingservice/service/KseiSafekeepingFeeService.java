package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.kseisafe.CreateKseiSafeRequest;
import com.bayu.billingservice.model.KseiSafekeepingFee;

import java.math.BigDecimal;
import java.util.List;

public interface KseiSafekeepingFeeService {

    KseiSafekeepingFee create(CreateKseiSafeRequest request);

    String readAndInsertToDB(String filePath);

    List<KseiSafekeepingFee> getAll();

    KseiSafekeepingFee getByCustomerCode(String customerCode);

    BigDecimal calculateAmountFeeByCustomerCodeAndMonthAndYear(String customerCode, String month, int year);

    BigDecimal calculateAmountFeeForLast3Months(String customerCode, String month, int year);

    String deleteAll();

}
