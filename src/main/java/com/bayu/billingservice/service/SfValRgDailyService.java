package com.bayu.billingservice.service;

import com.bayu.billingservice.model.SfValRgDaily;

import java.time.LocalDate;
import java.util.List;

public interface SfValRgDailyService {

    String readFileAndInsertToDB(String filePath, String monthYear);

    List<SfValRgDaily> getAll();

    List<SfValRgDaily> getAllByAid(String aid);

    List<SfValRgDaily> getAllByAidAndDate(String aid, LocalDate date);

    List<SfValRgDaily> getAllByAidAndMonthAndYear(String aid, String month, Integer year);

    List<SfValRgDaily> getAllByAidAndSecurityName(String aid, String securityName);

    String deleteAll();

}
