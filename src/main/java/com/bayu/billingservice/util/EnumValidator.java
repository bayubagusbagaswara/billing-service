package com.bayu.billingservice.util;

import com.bayu.billingservice.model.enumerator.BillingCategory;
import com.bayu.billingservice.model.enumerator.BillingTemplate;
import com.bayu.billingservice.model.enumerator.BillingType;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class EnumValidator {

    // Generic method to validate any enum type based on a string input
    private static <T extends Enum<T>> boolean validateEnum(Class<T> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumConstant.name().equalsIgnoreCase(value));
    }

    public static boolean validateEnumBillingCategory(String billingCategory) {
        return validateEnum(BillingCategory.class, billingCategory);
    }

    public static boolean validateEnumBillingType(String billingType) {
        return validateEnum(BillingType.class, billingType);
    }

    public static boolean validateEnumBillingTemplate(String billingTemplate) {
        return validateEnum(BillingTemplate.class, billingTemplate);
    }
}
