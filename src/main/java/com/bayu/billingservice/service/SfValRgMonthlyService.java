package com.bayu.billingservice.service;

import com.bayu.billingservice.model.SfValRgMonthly;

import java.util.List;

public interface SfValRgMonthlyService {

    String readFileAndInsertToDB(String filePath, String monthYear);

    List<SfValRgMonthly> getAll();

    List<SfValRgMonthly> getAllByAid(String aid);

    SfValRgMonthly getByAidAndSecurityName(String aid, String securityName);

    String deleteAll();
}
