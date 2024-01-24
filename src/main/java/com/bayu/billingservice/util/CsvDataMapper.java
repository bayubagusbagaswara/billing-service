package com.bayu.billingservice.util;

import com.bayu.billingservice.model.SKTransaction;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
@Slf4j
public class CsvDataMapper {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy");

    public static List<SKTransaction> mapCsvSKTransaction(List<String[]> rows) {
        List<SKTransaction> csvDataList = new ArrayList<>();
        log.info("Rows Size : {}", csvDataList.size());

        return null;
    }
}
