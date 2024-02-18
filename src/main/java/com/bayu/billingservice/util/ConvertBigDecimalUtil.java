package com.bayu.billingservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@UtilityClass
public class ConvertBigDecimalUtil {

    public static BigDecimal parseBigDecimalOrDefault(String value) {
        try {
            String normalized;
            if (null == value || 0 == value.trim().length()) {
                normalized = "0";
            } else {
                normalized = value;
            }
            return new BigDecimal(normalized);
        } catch (Exception e) {
            log.error("Parse BigDecimal is Failed : " + e.getMessage(), e);
            return null;
        }
    }

}
