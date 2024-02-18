package com.bayu.billingservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ConvertIntegerUtil {

    public static Integer parseIntOrDefault(String value) {
        try {
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (Exception e) {
            log.error("Parse Integer is Failed : " + e.getMessage(), e);
            return null;
        }
    }

}
