package com.bayu.billingservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class StringUtil {

    public static String processString(String value) {
        // Check if the value is null and return a default value if needed
        String processedValue = !value.isEmpty() ? value : "";

        // Trim the string if it is not null
        processedValue = processedValue.trim();

        return processedValue;
    }

    public static String removeWhitespaceBetweenCharacters(String input) {
        // Replace all whitespace between characters with an empty string
        return input.replaceAll("\\s+", "").trim();
    }

    public static String replaceBlanksWithUnderscores(String input) {
        // Replace blank characters with an underscore
        return input.replaceAll("\\s", "_");
    }
}
