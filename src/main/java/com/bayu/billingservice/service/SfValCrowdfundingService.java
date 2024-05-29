package com.bayu.billingservice.service;

import com.bayu.billingservice.model.SfValCrowdfunding;

import java.util.List;

public interface SfValCrowdfundingService {

    String readAndInsertToDB(String filePath, String monthYear);

    List<SfValCrowdfunding> getAll();

    List<SfValCrowdfunding> getAllByClientCode(String clientCode);

    // clientCode : VISIKU
    // month : November
    // year : 2023
    // VISIKU has many security codes
    List<SfValCrowdfunding> getAllByClientCodeAndMonthAndYear(String clientCode, String monthName, Integer year);

    String deleteAll();
}
