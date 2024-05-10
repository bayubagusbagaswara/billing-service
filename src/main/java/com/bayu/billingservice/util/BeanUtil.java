package com.bayu.billingservice.util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class BeanUtil {

    public static void copyAllProperties(Object source, Object target){
        BeanUtils.copyProperties(source, target);
    }

    public static void copyNotNullProperties(Object source, Object target){
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = srcWrapper.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = srcWrapper.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    // Source is data from request
    // Target is data from database
    public static void copyNotNullPropertiesV2(Object source, Object target) {
        BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        BeanWrapper targetWrapper = new BeanWrapperImpl(target);

        for (java.beans.PropertyDescriptor propertyDescriptor : srcWrapper.getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();
            Object srcValue = srcWrapper.getPropertyValue(propertyName);

            // Cek apakah nilai properti pada source tidak null
            if (srcValue != null) {
                // Set nilai properti pada target
                targetWrapper.setPropertyValue(propertyName, srcValue);
            }
        }
    }
}
