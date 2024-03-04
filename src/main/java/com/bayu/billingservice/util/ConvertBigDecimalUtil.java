package com.bayu.billingservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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

    public static String formattedVatFee(BigDecimal vatFee) {
        return String.format("%.0f", vatFee.multiply(BigDecimal.valueOf(100)));
    }

    public static String formattedBigDecimalToString(BigDecimal value) {
        String result;

        if (BigDecimal.ZERO.compareTo(value) == 0) {
            result = "0";
        } else {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');

            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);

            result = decimalFormat.format(value);
        }
        return result;
    }

}
